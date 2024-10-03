package yevhen.synii.admin_panel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import yevhen.synii.admin_panel.entity.UserEntity;

@SpringBootApplication
@EnableScheduling
public class AdminPanelApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdminPanelApplication.class, args);
	}

}
