# 使用CompletionService多线程异步执行提升系统性能

## 场景

随着互联网应用的深入，很多传统行业也都需要接入到互联网。我们公司也是这样，保险核心需要和很多保险中介对接，比如阿里、京东等等。这些公司对于接口服务的性能有些比较高的要求，传统的核心无法满足要求，所以信息技术部领导高瞻远瞩，决定开发互联网接入服务，满足来自性能的需求。

## 概念

- **CompletionService**将**Executor**和**BlockingQueue**的功能融合在一起，将**Callable**任务提交给**CompletionService**来执行，然后使用类似于队列操作的take和poll等方法来获得已完成的结果，而这些结果会在完成时被封装为Future。对于更多的概念，请参阅其他网络文档。

- 线程池的设计，阿里说过不要使用Java Executors 提供的默认线程池，因此需要更接近实际的情况来自定义一个线程池，根据多次压测，采用的线程池如下：

  ```java
  public ExecutorService getThreadPool(){
          return new ThreadPoolExecutor(75,
                  125,
                  180000,
                  TimeUnit.MILLISECONDS,
                  new LinkedBlockingDeque<>(450),
                  new ThreadPoolExecutor.CallerRunsPolicy());
      }
  ```

  **说明：**公司的业务为低频交易，对于单次调用性能要求高，但是并发压力根本不大，所以 阻塞队列已满且线程数达到最大值时所采取的饱和策略为调用者执行。

## 实现

### 业务

投保业务主要涉及这几个大的方面：投保校验、核保校验、保费试算

- 投保校验：最主要的是要查询客户黑名单和风险等级，都是千万级的表。而且投保人和被保人都需要校验
- 核保校验：除了常规的核保规则校验，查询千万级的大表，还需要调用外部智能核保接口获得用户的风险等级，投保人和被保人都需要校验
- 保费试算：需要计算每个险种的保费

### 设计

根据上面的业务，如果串行执行的话，单次性能肯定不高，所以考虑多线程异步执行获得校验结果，再对结果综合判断

- 投保校验：采用一个线程(也可以根据投保人和被保人数量来采用几个线程)
- 核保校验：
  - 常规校验：采用一个线程
  - 外部调用：有几个用户(指投保人和被保人)就采用几个线程
- 保费计算：有几个险种就采用几个线程，最后合并得到整个的保费

### 代码 

**以下代码是样例，实际逻辑已经去掉**

- 先创建投保、核保（常规、外部调用）、保费计算4个业务服务类：

  - 投保服务类：**InsuranceVerificationServiceImpl**，假设耗时50ms

    ```java
    @Service
    public class InsuranceVerificationServiceImpl implements InsuranceVerificationService {
        private static final Logger logger = LoggerFactory.getLogger(InsuranceVerificationServiceImpl.class);
        @Override
        public TaskResponseModel<Object> insuranceCheck(String key, PolicyModel policyModel) {
            try {
                //假设耗时50ms
                Thread.sleep(50);            
                return TaskResponseModel.success().setKey(key).setData(policyModel);
            } catch (InterruptedException e) {
                logger.warn(e.getMessage());            
                return TaskResponseModel.failure().setKey(key).setResultMessage(e.getMessage());
            }
        }
    }
    ```

  - 核保常规校验服务类：**UnderwritingCheckServiceImpl**，假设耗时50ms

    ```java
    @Service
    public class UnderwritingCheckServiceImpl implements UnderwritingCheckService {
        private static final Logger logger = LoggerFactory.getLogger(UnderwritingCheckServiceImpl.class);
        @Override
        public TaskResponseModel<Object> underwritingCheck(String key, PolicyModel policyModel) {
            try {
                //假设耗时50ms
                Thread.sleep(50);            
                return TaskResponseModel.success().setKey(key).setData(policyModel);
            } catch (InterruptedException e) {
                logger.warn(e.getMessage());            
                return TaskResponseModel.failure().setKey(key).setResultMessage(e.getMessage());
            }
        }
    }
    ```

  - 核保外部调用服务类：**ExternalCallServiceImpl**，假设耗时200ms

    ```java
    @Service
    public class ExternalCallServiceImpl implements ExternalCallService {
        private static final Logger logger = LoggerFactory.getLogger(ExternalCallServiceImpl.class);
        @Override
        public TaskResponseModel<Object> externalCall(String key, Insured insured) {
            try {
                //假设耗时200ms
                Thread.sleep(200);
                ExternalCallResultModel externalCallResultModel = new ExternalCallResultModel();
                externalCallResultModel.setIdcard(insured.getIdcard());
                externalCallResultModel.setScore(200);
                return TaskResponseModel.success().setKey(key).setData(externalCallResultModel);
            } catch (InterruptedException e) {
                logger.warn(e.getMessage());
                return TaskResponseModel.failure().setKey(key).setResultMessage(e.getMessage());
            }
        }
    }
    ```

  - 试算服务类：**TrialCalculationServiceImpl**，假设耗时50ms

    ```java
    @Service
    public class TrialCalculationServiceImpl implements TrialCalculationService {
        private static final Logger logger = LoggerFactory.getLogger(TrialCalculationServiceImpl.class);
        @Override
        public TaskResponseModel<Object> trialCalc(String key, Risk risk) {
            try {
                //假设耗时50ms
                Thread.sleep(50);
                return TaskResponseModel.success().setKey(key).setData(risk);
            } catch (InterruptedException e) {
                logger.warn(e.getMessage());
                return TaskResponseModel.failure().setKey(key).setResultMessage(e.getMessage());
            }
        }
    }
    
    ```

- 统一返回接口类：**TaskResponseModel**， 上面4个服务的方法统一返回**TaskResponseModel**

  ```java
  @Data
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @EqualsAndHashCode
  @Accessors(chain = true)
  public class TaskResponseModel<T extends Object> implements Serializable {
      private String key;           //唯一调用标志
      private String resultCode;    //结果码
      private String resultMessage; //结果信息
      private T data;               //业务处理结果
  
      public static TaskResponseModel<Object> success() {
          TaskResponseModel<Object> taskResponseModel = new TaskResponseModel<>();
          taskResponseModel.setResultCode("200");
          return taskResponseModel;
      }
      public static TaskResponseModel<Object> failure() {
          TaskResponseModel<Object> taskResponseModel = new TaskResponseModel<>();
          taskResponseModel.setResultCode("400");
          return taskResponseModel;
      }
  }
  ```

  **注：**

  1.  **key**为这次调用的唯一标识，由调用者传进来
  2. **resultCode**结果码，200为成功，400表示有异常
  3. **resultMessage**信息，表示不成功或者异常信息
  4. **data**业务处理结果，如果成功的话
  5. 这些服务类都是单例模式

- 要使用用**CompletionService**的话，需要创建实现了**Callable**接口的线程

  - 投保**Callable**: 

    ```java
    @Data
    @AllArgsConstructor
    public class InsuranceVerificationCommand implements Callable<TaskResponseModel<Object>> {
        private String key;
        private PolicyModel policyModel;
        private final InsuranceVerificationService insuranceVerificationService;
        @Override
        public TaskResponseModel<Object> call() throws Exception {
            return insuranceVerificationService.insuranceCheck(key, policyModel);
        }
    }
    ```

  - 核保常规校验**Callable**:

    ```java
    @Data
    @AllArgsConstructor
    public class UnderwritingCheckCommand implements Callable<TaskResponseModel<Object>> {
        private String key;
        private PolicyModel policyModel;
        private final UnderwritingCheckService underwritingCheckService;
        @Override
        public TaskResponseModel<Object> call() throws Exception {
            return underwritingCheckService.underwritingCheck(key, policyModel);
        }
    }
    ```

  - 核保外部调用**Callable**:

    ```java
    @Data
    @AllArgsConstructor
    public class ExternalCallCommand implements Callable<TaskResponseModel<Object>> {
        private String key;
        private Insured insured;
        private final ExternalCallService externalCallService;
        @Override
        public TaskResponseModel<Object> call() throws Exception {
            return externalCallService.externalCall(key, insured);
        }
    }
    ```

  - 试算调用**Callable**:

    ```java
    @Data
    @AllArgsConstructor
    public class TrialCalculationCommand implements Callable<TaskResponseModel<Object>> {
        private String key;
        private Risk risk;
        private final TrialCalculationService trialCalculationService;
        @Override
        public TaskResponseModel<Object> call() throws Exception {
            return trialCalculationService.trialCalc(key, risk);
        }
    }
    ```

    **注**：

    1. 每一次调用，需要创建这4种**Callable**

    2. 返回统一接口**TaskResopnseModel**

       

- 异步执行的类：**TaskExecutor**

  ```java
  @Component
  public class TaskExecutor {
      private static final Logger logger = LoggerFactory.getLogger(TaskExecutor.class);
      //线程池
      private final ExecutorService executorService;
  
      public TaskExecutor(ExecutorService executorService) {
          this.executorService = executorService;
      }
  
      //异步执行，获取所有结果后返回
      public List<TaskResponseModel<Object>> execute(List<Callable<TaskResponseModel<Object>>> commands) {
          //创建异步执行对象
          CompletionService<TaskResponseModel<Object>> completionService = new ExecutorCompletionService<>(executorService);
          for (Callable<TaskResponseModel<Object>> command : commands) {
              completionService.submit(command);
          }
          //获取所有异步执行线程的结果
          int taskCount = commands.size();
          List<TaskResponseModel<Object>> params = new ArrayList<>(taskCount);
          try {
              for (int i = 0; i < taskCount; i++) {
                  Future<TaskResponseModel<Object>> future = completionService.take();
                  params.add(future.get());
              }
          } catch (InterruptedException | ExecutionException e) {
              //异常处理
              params.clear();
              params.add(TaskResponseModel.failure().setKey("error").setResultMessage("异步执行线程错误"));
          }
          //返回，如果执行中发生error, 则返回相应的key值： error
          return params;
      }
  }
  
  ```

  **注**：

  1. 为单例模式
  2. 接收参数为**List<Callable<TaskResponseModel<Object>>>**，也就是上面定义的4种Callable的列表
  3. 返回**List<TaskResponseModel<Object>>**，也就是上面定义4种Callable返回的结果列表
  4. 我们的业务是对返回结果统一判断，业务返回结果有因果关系
  5. 如果线程执行有异常，也返回List<TaskResponseModel>，这个时候列表中只有一个**TaskResponseModel**，**key**为error, 后续调用者可以通过这个来判断线程是否执行成功；

- 调用方：CompletionServiceController

  ```java
  @RestController
  public class CompletionServiceController {
      //投保key
      private static final String INSURANCE_KEY = "insurance_";
      //核保key
      private static final String UNDERWRITING_KEY = "underwriting_";
      //外部调用key
      private static final String EXTERNALCALL_KEY = "externalcall_";
      //试算key
      private static final String TRIA_KEY = "trial_";
  
      private static final Logger logger = LoggerFactory.getLogger(CompletionServiceController.class);
      
      private final ExternalCallService externalCallService;
      private final InsuranceVerificationService insuranceVerificationService;
      private final TrialCalculationService trialCalculationService;
      private final UnderwritingCheckService underwritingCheckService;
      private final TaskExecutor taskExecutor;
  
      public CompletionServiceController(ExternalCallService externalCallService, InsuranceVerificationService insuranceVerificationService, TrialCalculationService trialCalculationService, UnderwritingCheckService underwritingCheckService, TaskExecutor taskExecutor) {
          this.externalCallService = externalCallService;
          this.insuranceVerificationService = insuranceVerificationService;
          this.trialCalculationService = trialCalculationService;
          this.underwritingCheckService = underwritingCheckService;
          this.taskExecutor = taskExecutor;
      }
  
      //多线程异步并发接口
      @PostMapping(value = "/async", headers = "Content-Type=application/json;charset=UTF-8")
      public String asyncExec(@RequestBody PolicyModel policyModel) {
          long start = System.currentTimeMillis();
  
          asyncExecute(policyModel);
          logger.info("异步总共耗时：" + (System.currentTimeMillis() - start));
          return "ok";
      }
  
      //串行调用接口
      @PostMapping(value = "/sync", headers = "Content-Type=application/json;charset=UTF-8")
      public String syncExec(@RequestBody PolicyModel policyModel) {
          long start = System.currentTimeMillis();
          syncExecute(policyModel);
          logger.info("同步总共耗时：" + (System.currentTimeMillis() - start));
          return "ok";
      }
      private void asyncExecute(PolicyModel policyModel) {
          List<Callable<TaskResponseModel<Object>>> baseTaskCallbackList = new ArrayList<>();
          //根据被保人外部接口调用
          for (Insured insured : policyModel.getInsuredList()) {
              ExternalCallCommand externalCallCommand = new ExternalCallCommand(EXTERNALCALL_KEY + insured.getIdcard(), insured, externalCallService);
              baseTaskCallbackList.add(externalCallCommand);
          }
          //投保校验
          InsuranceVerificationCommand insuranceVerificationCommand = new InsuranceVerificationCommand(INSURANCE_KEY, policyModel, insuranceVerificationService);
          baseTaskCallbackList.add(insuranceVerificationCommand);
          //核保校验
          UnderwritingCheckCommand underwritingCheckCommand = new UnderwritingCheckCommand(UNDERWRITING_KEY, policyModel, underwritingCheckService);
          baseTaskCallbackList.add(underwritingCheckCommand);
          //根据险种进行保费试算
          for(Risk risk : policyModel.getRiskList()) {
              TrialCalculationCommand trialCalculationCommand = new TrialCalculationCommand(TRIA_KEY + risk.getRiskcode(), risk, trialCalculationService);
              baseTaskCallbackList.add(trialCalculationCommand);
          }
          List<TaskResponseModel<Object>> results = taskExecutor.execute(baseTaskCallbackList);
          for (TaskResponseModel<Object> t : results) {
              if (t.getKey().equals("error")) {
                  logger.warn("线程执行失败");
                  logger.warn(t.toString());
              }
              logger.info(t.toString());
          }
  
      }
      private void syncExecute(PolicyModel policyModel) {
          //根据被保人外部接口调用
          for (Insured insured : policyModel.getInsuredList()) {
              TaskResponseModel<Object> externalCall = externalCallService.externalCall(insured.getIdcard(), insured);
              logger.info(externalCall.toString());
          }
          //投保校验
          TaskResponseModel<Object> insurance = insuranceVerificationService.insuranceCheck(INSURANCE_KEY, policyModel);
          logger.info(insurance.toString());
          //核保校验
          TaskResponseModel<Object> underwriting = underwritingCheckService.underwritingCheck(UNDERWRITING_KEY, policyModel);
          logger.info(underwriting.toString());
          //根据险种进行保费试算
          for(Risk risk : policyModel.getRiskList()) {
              TaskResponseModel<Object> risktrial = trialCalculationService.trialCalc(risk.getRiskcode(), risk);
              logger.info(risktrial.toString());
          }
  
      }
  }
  ```

  **注**：

  1. 为测试方便，提供两个接口调用：一个是串行执行，一个是异步并发执行
  2. 在异步并发执行函数**asyncExecute**中：
     1. 根据有多少个被保人，创建多少个外部调用的Callable实例，**key**值为**EXTERNALCALL_KEY + insured.getIdcard()**，在一次保单投保调用中，每一个被保人**Callable**的**key**是不一样的。
     2. 根据有多少个险种，创建多少个试算的**Callable**实例，**key**为**TRIA_KEY + risk.getRiskcode()**，在一次保单投保调用中，每一个险种的**Callable**的key是不一样的
     3. 创建投保校验的**Callable**实例，业务上只需要一个
     4. 创建核保校验的**Callable**实例，业务上只需要一个
     5. 将Callable列表传入到**TaskExecutor**执行异步并发调用
     6. 根据返回结果来判断，通过判断返回的**TaskResponseModel**的**key**值可以知道是哪类业务校验，分布进行判断，还可以交叉判断（公司的业务就是要交叉判断）

  

## 验证

- 验证数据：

  ```json
  {"insuredList":[{"idcard":"laza","name":"320106"},{"idcard":"ranran","name":"120102"}],"policyHolder":"lazasha","policyNo":"345000987","riskList":[{"mainFlag":1,"premium":300,"riskcode":"risk001","riskname":"险种一"},{"mainFlag":0,"premium":400,"riskcode":"risk002","riskname":"险种二"}]}
  ```

  上面数据表明：有两个被保人，两个险种。按照我们上面的定义，会调用两次外部接口，两次试算，一次投保，一次核保。而在样例代码中，一次外部接口调用耗时为200ms, 其他都为50ms.

  **本地开发的配置为8C16G:**

  - 同步串行接口调用计算： 2 * 200 + 2 * 50 + 50 + 50 = 600ms

  - 多线程异步执行调用计算： 按照多线程并发执行原理，取耗时最长的200ms

    

- 验证：同步接口

  ![service-1](使用CompletionService异步执行提升系统性能.assets/service-1.JPG)

  输出耗时：可以看到耗时**601ms**

  ![service-3](使用CompletionService异步执行提升系统性能.assets/service-3.JPG)

- 验证：多线程异步执行接口

  ![service-2](使用CompletionService异步执行提升系统性能.assets/service-2.JPG)

  输出耗时：可以看到为**204ms**

  ![service-4](使用CompletionService异步执行提升系统性能.assets/service-4.JPG)

  结果：基本和我们的预期相符合。

## 结束

这是将实际生产中的例子简化出来，具体生产的业务比较复杂，不便于展示。实际情况下，原来的接口需要1000ms以上才能完成单次调用，有的需要2000-3000ms。现在的接口，在生产两台8c16g的虚拟机, 经过4个小时的简单压测能够支持2000用户并发，单次返回时长为350ms左右，服务很稳定，完全能够满足公司的业务发展需求。提供的这个是可以运行的列子，代码在：https://github.com/lazasha111211/completionservice-demo.git
