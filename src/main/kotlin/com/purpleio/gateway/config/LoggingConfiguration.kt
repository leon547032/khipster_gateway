package com.purpleio.gateway.config

import ch.qos.logback.classic.LoggerContext
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.jhipster.config.JHipsterProperties
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.info.BuildProperties
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.context.annotation.Configuration

import io.github.jhipster.config.logging.LoggingUtils.addContextListener
import io.github.jhipster.config.logging.LoggingUtils.addJsonConsoleAppender
import io.github.jhipster.config.logging.LoggingUtils.addLogstashTcpSocketAppender
import io.github.jhipster.config.logging.LoggingUtils.setMetricsMarkerLogbackFilter

/*
 * Configures the console and Logstash log appenders from the app properties.
 */
@Configuration
@RefreshScope
class LoggingConfiguration(
    @Value("\${spring.application.name}") appName: String,
    @Value("\${server.port}") serverPort: String,
    jHipsterProperties: JHipsterProperties,
    buildProperties: BuildProperties?,
    mapper: ObjectMapper
) {
    init {
        val context = LoggerFactory.getILoggerFactory() as LoggerContext

        val map = mutableMapOf<String, String?>()
        map["app_name"] = appName
        map["app_port"] = serverPort
        buildProperties?.apply { map["version"] = this.version }
        val customFields = mapper.writeValueAsString(map)

        val loggingProperties = jHipsterProperties.logging
        val logstashProperties = loggingProperties.logstash

        if (loggingProperties.isUseJsonFormat) {
            addJsonConsoleAppender(context, customFields)
        }
        if (logstashProperties.isEnabled) {
            addLogstashTcpSocketAppender(context, customFields, logstashProperties)
        }
        if (loggingProperties.isUseJsonFormat || logstashProperties.isEnabled) {
            addContextListener(context, customFields, loggingProperties)
        }
        if (jHipsterProperties.metrics.logs.isEnabled) {
            setMetricsMarkerLogbackFilter(context, loggingProperties.isUseJsonFormat)
        }
    }
}