package com.batch.sandbox

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.oxm.jaxb.Jaxb2Marshaller
import javax.xml.stream.XMLInputFactory

@Configuration
@EnableBatchProcessing
class JobConfig {

    @Autowired
    private lateinit var jobBuilderFactory: JobBuilderFactory

    @Autowired
    private lateinit var stepBuilderFactory: StepBuilderFactory

    @Bean
    fun myJob(mainStep: Step): Job {
        return jobBuilderFactory
            .get("my-job")
            .start(mainStep)
            .build()
    }

//    @Bean
//    @StepScope
//    fun reader(
//        @Value("#{jobParameters['file.input']}") input: String
//    ): ItemReader<Person> {
//        val lineMapper = DefaultLineMapper<Person>()
//
//        val tokenizer = FixedLengthTokenizer()
//        tokenizer.setColumns(Range(1, 10), Range(11, 16))
//        tokenizer.setNames("name", "age")
//
//        lineMapper.setLineTokenizer(tokenizer)
//        lineMapper.setFieldSetMapper { fieldSet ->
//            val age = fieldSet.readInt("age")
//            Person(
//                name = fieldSet.readString("name"),
//                age = age
//            )
//        }
//
//        val itemReader = FlatFileItemReader<Person>()
//        itemReader.setLineMapper(lineMapper)
//        itemReader.setResource(FileSystemResource(input))
//        return itemReader
//    }

    @Bean
    fun reader():ItemReader<Person> {

        val jaxb2Marshaller = Jaxb2Marshaller()
        jaxb2Marshaller.setClassesToBeBound(Person::class.java)

        val reader = StaxEventItemReaderBuilder<Person>()
            .name("personReader")
            .resource(FileSystemResource("input.xml"))
            .addFragmentRootElements("person")
            .unmarshaller(jaxb2Marshaller)
            .build()
        
        return reader
        
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
    fun mainStep(
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
