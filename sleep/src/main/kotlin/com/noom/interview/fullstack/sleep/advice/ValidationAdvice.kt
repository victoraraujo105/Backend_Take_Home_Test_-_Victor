package com.noom.interview.fullstack.sleep.advice

import com.fasterxml.jackson.core.JacksonException
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class ValidationAdvice {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val errors = ex.bindingResult.fieldErrors.filter {
            it.defaultMessage != null
        }.groupBy {
            it.field
        }.mapValues {
            it.value.map {
                it.defaultMessage
            }
        }
        println(errors)
        println(status)
        return ResponseEntity(errors, headers, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [InvalidFormatException::class, MismatchedInputException::class])
    fun handleInvalidFormatException(
        ex: JsonMappingException,
    ): ResponseEntity<Any> {
        val errors = mapOf(
            "error" to "Invalid input",
            "message" to ex.originalMessage
        )
        println(errors)
        return ResponseEntity(errors, HttpStatus.BAD_REQUEST)
    }

}