package com.smarttaskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.CommandLineRunner;
import com.odev.taskmanager.service.TaskService;
import com.odev.taskmanager.model.Task;


@SpringBootApplication
@EnableScheduling
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
          //  Task task1 = new Task("Study Java", "Complete the inheritance exercises", "HIGH", "java.tester.odev@gmail.com");
            //  Task task2 = new Task("Read a book", "Read 20 pages of a novel", "LOW", "java.tester.odev@gmail.com");

            //taskService.saveTask(task1);
            //taskService.saveTask(task2);
        };
    }
   
}
