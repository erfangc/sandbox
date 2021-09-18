package com.batch.sandbox

import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "person")
class Person {
    @get:XmlElement
    var name: String? = null

    @get:XmlElement
    var age: Int? = null
}
