package com.mercadolibre.mutant.application.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validador customizado para secuencias de ADN
 * Implementa validaciones a nivel de entrada antes de procesar
 */
public class DnaValidator implements ConstraintValidator<ValidDna, String[]> {

    private static final Pattern VALID_DNA_PATTERN = Pattern.compile("^[ATCG]+$");

    @Override
    public boolean isValid(String[] dna, ConstraintValidatorContext context) {
        // Null check
        if (dna == null || dna.length == 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("DNA sequence cannot be null or empty")
                   .addConstraintViolation();
            return false;
        }

        int n = dna.length;

        // Validar cada fila
        for (int i = 0; i < n; i++) {
            String sequence = dna[i];
            
            // Validar que no sea null
            if (sequence == null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("DNA sequence row cannot be null")
                       .addConstraintViolation();
                return false;
            }

            // Validar matriz cuadrada NxN
            if (sequence.length() != n) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                    String.format("DNA must be NxN matrix. Expected size: %d, but row %d has size: %d", 
                                  n, i, sequence.length()))
                       .addConstraintViolation();
                return false;
            }

            // Validar solo caracteres ATCG (case-sensitive)
            if (!VALID_DNA_PATTERN.matcher(sequence).matches()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                    String.format("DNA sequence contains invalid characters in row %d. Only A, T, C, G are allowed", i))
                       .addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}
