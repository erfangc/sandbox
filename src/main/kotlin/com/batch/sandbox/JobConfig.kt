package com.batch.sandbox

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper
import org.springframework.batch.item.file.mapping.DefaultLineMapper
import org.springframework.batch.item.file.mapping.FieldSetMapper
import org.springframework.batch.item.file.transform.FixedLengthTokenizer
import org.springframework.batch.item.file.transform.Range
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.FileSystemResource

@Configuration
@EnableBatchProcessing
class JobConfig {
    @Autowired
    private lateinit var jobBuilderFactory: JobBuilderFactory

    @Autowired
    private lateinit var stepBuilderFactory: StepBuilderFactory

    @Bean
    fun myJob(myStep: Step): Job {
        return jobBuilderFactory.get("my-job").start(myStep).build()
    }

    @Bean
    @StepScope
    fun reader(
        @Value("#{jobParameters['file.input']}") input: String
    ): FlatFileItemReader<Person> {
        val lineMapper = DefaultLineMapper<Person>()
        val tokenizer = FixedLengthTokenizer()
        tokenizer.setColumns(
            Range(1, 10),
            Range(11, 16)
        )
        tokenizer.setNames("name", "age")

        lineMapper.setLineTokenizer(tokenizer)
        lineMapper.setFieldSetMapper { fieldSet ->
            Person(
                name = fieldSet.readString("name"),
                age = fieldSet.readInt("age")
            )
        }

        val itemReader = FlatFileItemReader<Person>()
        itemReader.setLineMapper(lineMapper)
        itemReader.setResource(FileSystemResource(input))
        return itemReader

    }

    @Bean
    fun writer(): ItemWriter<Person> {
        return ItemWriter { items ->
            items.forEach { person ->
                println(person)
            }
        }
    }

    @Bean
    fun myStep(
        reader: ItemReader<Person>,
        writer: ItemWriter<Person>
    ): Step {
        return stepBuilderFactory
            .get("load-file")
            .chunk<Person, Person>(1)
            .reader(reader)
            .writer(writer)
            .build()
    }

}