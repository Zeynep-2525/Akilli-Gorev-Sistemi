package com.smarttaskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.CommandLineRunner;
import com.odev.taskmanager.service.TaskService;
import com.odev.taskmanager.model.Task;
import org.springframework.context.annotation.ComponentScan;
import com.odev.taskmanager.model.TaskPriority;
import java.time.LocalDateTime;




@SpringBootApplication(scanBasePackages = "com.odev")
@EnableScheduling
@ComponentScan(basePackages = {"com.odev.taskmanager", "com.odev.service"})


public class TaskManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagerApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    
   
    @Bean
    public CommandLineRunner loadData(TaskService taskService) {
        return args -> {
            Task task1 = new Task(
                "Study Java",
                "Complete the inheritance exercises",
                TaskPriority.HIGH,
                "senanazkaya0@gmail.com",
                LocalDateTime.now().plusDays(7),
                false
            );

            Task task2 = new Task(
                "Read a book",
                "Read 20 pages of a novel",
                TaskPriority.LOW,
                "senanazkaya0@gmail.com",
                LocalDateTime.now().plusDays(3),
                false
            );

            taskService.saveTask(task1);
            taskService.saveTask(task2);
        };
    }


   
}
