package io.github.dongguabai.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author Dongguabai
 * @Description
 * @Date 创建于 2020-06-20 00:27
 */
@SpringBootApplication
@MapperScan(basePackages = {"com.dongguabai.dongguabaitask.server.persistence.repository"})
public class DongguabaiTaskApplication {

	public static void main(String[] args) {
		SpringApplication.run(DongguabaiTaskApplication.class, args);
	}

}
