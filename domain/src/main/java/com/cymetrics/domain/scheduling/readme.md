```java

// in some other application which needs to add new scheduling
@Inject
ScheduleService scheduleService ;

Scheduler s = scheduleService.register( new IScheduledTask(String[] args){
            // Do Something by parse the args
            int int1 = Integer.parse(args[0]);
            float float1 = float.parse(args[1]);
            print(int1+float2);
        } , "my scheduled task" , "* 10 * * * *" , {"2","2.0"});

// modify args
s.setArgs({"3","3.0"});

// or remove some scheduling
scheduleService.remove("my scheduled task");





// scheduler main function in application

public static void main(String[] args){
        SchedulerRepository repo=new SchedulerRepository();    
        while(true){
            try{
                now=getCurrentInstant();
                schedulers=repo.getAll();
                for(scheduler in schedulers){
                    start transaction
                    if(scheduler.shouldExecute){
                        scheduler.execute();
                    }
                    commit transaction
                }
            }
            catch Exceptnio e{
                log the error
                rollback transaction
            }   
        }
}

```