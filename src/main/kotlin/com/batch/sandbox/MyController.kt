package com.batch.sandbox

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParameter
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class MyController(
    private val jobLauncher: JobLauncher,
    private val myJob: Job,
) {

    @PostMapping
    @RequestMapping("launch-job")
    fun launchJob(@RequestParam input: String) {
        jobLauncher.run(myJob, JobParameters(mapOf("file.input" to JobParameter(input))))
    }

}