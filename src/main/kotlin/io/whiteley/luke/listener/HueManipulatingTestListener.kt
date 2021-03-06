package io.whiteley.luke.listener

import io.github.zeroone3010.yahueapi.Hue
import io.whiteley.luke.config.TdlPluginExtension
import io.whiteley.luke.service.HueService
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestResult

open class HueManipulatingTestListener(
    private val logger: Logger,
    private val ext: TdlPluginExtension,
    private var hueService: HueService? = null
) : LightManipulatingTestListener() {

    override fun afterSuite(suite: TestDescriptor, result: TestResult) {
        if (ext.hueApiKey == null || ext.bridgeIp == null) {
            logger.error("tdl failed: Both hueApiKey and bridgeIp must be supplied")
            return
        }

        val hue = Hue(ext.bridgeIp!!, ext.hueApiKey!!)
        hueService = hueService ?: HueService(hue)

        // A null parent indicates all tests have been run
        if (suite.parent == null) {
            try {
                logger.info("tdl sending result $result to room ${ext.roomName}")
                hueService?.sendResultToRoom(result.resultType, ext.roomName!!)
            } catch (e: Exception) {
                logger.error("${e.message}")
            }
        }
    }
}
