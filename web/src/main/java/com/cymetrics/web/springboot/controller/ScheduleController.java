package com.cymetrics.web.springboot.controller;

import com.cymetrics.domain.scheduling.services.ScheduleService;
import com.cymetrics.web.springboot.controller.error.ErrorCode;
import com.cymetrics.web.springboot.controller.utils.ResponseUtils;
import com.cymetrics.web.springboot.dto.Schedule;
import com.cymetrics.web.springboot.requestbody.ScheduleRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.ArrayList;

@RestController
public class ScheduleController {

    @Inject
    ScheduleService service;

    @PostMapping("/schedules")
    public ResponseEntity newSchedule(@RequestBody ScheduleRequest scheduleRequest) {
        var result = this.service.createSchedule(
                scheduleRequest.getName(),
                scheduleRequest.getCommand(),
                scheduleRequest.getCronExpression()
        );
        if (result.isEmpty()) {
            return ResponseUtils.wrapFailResponse("Invalid schedule", ErrorCode.CREATE_USER_FAILS);
        }

        Schedule s = new Schedule(
                result.get().getName(),
                result.get().getCommand(),
                result.get().getCronExpression().getStringExpression()
        );
        return ResponseUtils.wrapSuccessResponse(s);
    }

    @GetMapping("/schedules")
    public ResponseEntity getSchedule() {
        var schedules = service.getAllSchedules();
        ArrayList<Schedule> scheduleDTO = new ArrayList<>();
        for (var s : schedules) {
            Schedule dto = new Schedule(s.getName(), s.getCommand(), s.getCronExpression().getStringExpression());
            scheduleDTO.add(dto);
        }
        return ResponseUtils.wrapSuccessResponse(scheduleDTO);
    }

    @DeleteMapping("/schedules/{name}")
    public ResponseEntity getSchedule(@PathVariable("name") String name) {
        service.removeSchedule(name);
        return ResponseUtils.wrapSuccessResponse("Delete schedule finished");
    }
}
