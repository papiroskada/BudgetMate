package com.example.PersonalAccounting;

import com.example.PersonalAccounting.services.finantial_arrangement_calculations.FinancialArrangementStartEndTransactionCreator;
import com.example.PersonalAccounting.services.finantial_arrangement_calculations.impl.CreditCalculations;
import com.example.PersonalAccounting.services.finantial_arrangement_calculations.impl.CreditStartEndTransactionCreator;
import com.example.PersonalAccounting.services.finantial_arrangement_calculations.impl.DepositCalculations;
import com.example.PersonalAccounting.services.finantial_arrangement_calculations.FinancialArrangementCalculations;
import com.example.PersonalAccounting.services.finantial_arrangement_calculations.impl.DepositStartEndTransactionCreator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;

//TODO: add pagination
//TODO: try to move @exceptionHandler in crud controllers somewhere
//TODO: check why notSuchEl exception stacktrace print in console through handler

//TODO: maybe move @EnableScheduling to schedule own config

@SpringBootApplication
@EnableScheduling
public class PersonalAccountingApplication {

	private final Environment environment;

	@Autowired
	public PersonalAccountingApplication(Environment environment) {
		this.environment = environment;
	}

	public static void main(String[] args) {
		SpringApplication.run(PersonalAccountingApplication.class, args);
	}

	//TODO: remove list bean

	@Bean
	@Autowired
	public List<FinancialArrangementCalculations> financialArrangementCalculationsList(CreditCalculations credit,
																					   DepositCalculations deposit) {
		return List.of(credit, deposit);
	}

	@Bean
	@Autowired
	public List<FinancialArrangementStartEndTransactionCreator> financialArrangementTransactionCreatorList(CreditStartEndTransactionCreator credit,
																										   DepositStartEndTransactionCreator deposit) {
		return List.of(credit, deposit);
	}

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(Objects.requireNonNull(environment.getProperty("spring.datasource.driver-class-name")));
		dataSource.setUrl(environment.getProperty("spring.datasource.url"));
		dataSource.setUsername(environment.getProperty("spring.datasource.username"));
		dataSource.setPassword(environment.getProperty("spring.datasource.password"));

		return dataSource;
	}

	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {return new JdbcTemplate(dataSource);}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}
