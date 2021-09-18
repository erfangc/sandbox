package com.batch.sandbox

import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ExecutionContext
import org.springframework.batch.item.xml.StaxEventItemReader
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.FileSystemResource
import org.springframework.oxm.jaxb.Jaxb2Marshaller
import org.springframework.stereotype.Component

@Component("staxItemReader")
@StepScope
class StaxItemReader : StaxEventItemReader<Person>() {
    
    @Value("#{jobParameters['file.input']}")
    private lateinit var input: String

    private val jaxb2Marshaller = Jaxb2Marshaller()
    
    init {
        jaxb2Marshaller.setClassesToBeBound(Person::class.java)
        this.setUnmarshaller(jaxb2Marshaller)
        this.setName("personReader")
        this.setFragmentRootElementName("person")
    }

    override fun open(executionContext: ExecutionContext) {
        this.setResource(FileSystemResource(input))
        super.open(executionContext)
    }
    
}