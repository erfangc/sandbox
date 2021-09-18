package com.batch.sandbox

import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParameter
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.launch.JobInstanceAlreadyExistsException
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File

@RestController
class MyController(
    private val jobLauncher: JobLauncher,
    private val myJob: Job,
) {

    data class Result(
        val filename: String,
        val status: BatchStatus,
        val message: String? = null,
    )

    private val dir = File("inbound")

    @PostMapping
    @RequestMapping("launch-job")
    fun launchJob(): List<Result> {
        return dir
            .list { _, name -> name.endsWith(".xml") }
            .map { filename ->
                val file = File(dir, filename)
                try {
                    val execution =
                        jobLauncher.run(
                            myJob, JobParameters(mapOf("file.input" to JobParameter(file.absolutePath)))
                        )
                    Result(
                        filename = file.absolutePath,
                        status = execution.status,
                    )
                } catch (e: Exception) {
                    if (e is JobInstanceAlreadyExistsException) {
                        Result(
                            filename = file.absolutePath,
                            status = BatchStatus.COMPLETED,
                            message = e.message,
                        )
                    } else {
                        Result(
                            filename = file.absolutePath,
                            status = BatchStatus.UNKNOWN,
                            message = e.message,
                        )
                    }
                }
            }

    }

}