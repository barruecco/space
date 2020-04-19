package com.example.space;

import org.apache.tomcat.jni.Proc;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class KafkaListener {

    final
    SpaceRepository spaceRepository;

    public KafkaListener(SpaceRepository spaceRepository) {
        this.spaceRepository = spaceRepository;
    }

    @StreamListener(Processor.INPUT)
    public void onEmployeePassedEvent(@Payload EmployeePassed employeePassed) {
        if (employeePassed.getEventType().equals("EmployeePassed")) {
            String from = employeePassed.getFrom();

            Optional<Space> optionalFromSpace = spaceRepository.findById(from);
            Space fromSpace = optionalFromSpace.get();
            fromSpace.setCount(fromSpace.getCount() - 1);

            spaceRepository.save(fromSpace);

            String to = employeePassed.getTo();
            Optional<Space> optionalToSpace = spaceRepository.findById(to);
            Space toSpace = optionalToSpace.get();
            toSpace.setCount(toSpace.getCount() + 1);

            spaceRepository.save(toSpace);
        }
    }

    @StreamListener(Processor.INPUT)
    public void onGateInstalledEvent(@Payload GateInstalled gateInstalled) {
        if(gateInstalled.getEventType().equals("GateInstalled")) {
            String from = gateInstalled.getFrom();
            String to = gateInstalled.getTo();
            checkAndSave(from);
            checkAndSave(to);
        }
    }

    public void checkAndSave(String spaceName) {
        Optional<Space> optionalFromSpace = spaceRepository.findById(spaceName);
        if(!optionalFromSpace.isPresent()){
            Space fromSpace = new Space();
            fromSpace.setName(spaceName);
            fromSpace.setCount(0);
            spaceRepository.save(fromSpace);
        }
    }
}
