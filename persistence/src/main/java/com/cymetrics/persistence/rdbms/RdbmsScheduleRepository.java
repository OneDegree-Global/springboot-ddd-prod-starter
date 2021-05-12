package com.cymetrics.persistence.rdbms;

import com.cymetrics.domain.scheduling.aggregates.Schedule;
import com.cymetrics.domain.scheduling.exception.InvalidCronException;
import com.cymetrics.domain.scheduling.repository.ScheduleRepository;
import com.cymetrics.persistence.rdbms.dao.ScheduleDao;
import com.cymetrics.persistence.rdbms.entities.ScheduleDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Optional;

public class RdbmsScheduleRepository implements ScheduleRepository {

    @Inject
    ScheduleDao scheduleDao;
    private static Logger logger = LoggerFactory.getLogger(RdbmsUserRepository.class);

    ScheduleDo convertToScheduleDo(Schedule s){
        Timestamp effectiveTimeStamp;
        if(s.getEffectiveTime()==null){
            effectiveTimeStamp = null;
        }
        else{
            effectiveTimeStamp = Timestamp.valueOf(s.getEffectiveTime().toLocalDateTime());
        }
        ScheduleDo scheduleDo = new ScheduleDo(s.getName(),
                s.getCommand(),
                s.isActive(),
                s.isReProducible(),
                effectiveTimeStamp,
                String.join(" ",s.getArgs()),
                s.getCronExpression().getStringExpression()
        );
        return scheduleDo;
    }

    Optional<Schedule> convertToSchedule(ScheduleDo scheduleDo){
        try {
            Schedule schedule = new Schedule(scheduleDo.getName(), scheduleDo.getCommand(), scheduleDo.getCronExpression());
            schedule.setActive(scheduleDo.getActive());
            schedule.setReProducible(scheduleDo.getIsReProducible());
            schedule.setArgs(scheduleDo.getArgs().split(" "));
            if(scheduleDo.getEffectiveTime()!=null)
                schedule.setEffectiveTime(scheduleDo.getEffectiveTime().toLocalDateTime().atZone(ZoneId.systemDefault()));
            return Optional.of(schedule);
        } catch (InvalidCronException e){
            logger.error("invalid cron when converting scheduleDo to Schedule"+e.toString());
            return Optional.empty();
        }
    }

    @Override
    public ArrayList<Schedule> getAll() {
        ArrayList<ScheduleDo> scheduleDos = (ArrayList<ScheduleDo>) scheduleDao.findAll();
        ArrayList<Schedule> schedules = new ArrayList<>();
        for(ScheduleDo scheduleDo : scheduleDos){
            Optional<Schedule> s = convertToSchedule(scheduleDo);
            if(s.isPresent())
                schedules.add(s.get());
        }
        return schedules;
    }

    @Override
    public Optional<Schedule> getByName(String name) {
        Optional<ScheduleDo> scheduleDo = scheduleDao.findByName(name);
        if(scheduleDo.isEmpty())
            return Optional.empty();
        return convertToSchedule(scheduleDo.get());
    }

    @Override
    public Optional<Schedule> save(Schedule schedule) {
        ScheduleDo scheduleDo = convertToScheduleDo(schedule);
        System.out.println("Save schedule DO:");
        ScheduleDo savedDo = scheduleDao.save(scheduleDo);
        if(savedDo==null)
            return Optional.empty();
        return convertToSchedule(savedDo);
    }


    @Override
    @Transactional
    public void deleteByName(String name) {
        scheduleDao.deleteByName(name);
    }
}
