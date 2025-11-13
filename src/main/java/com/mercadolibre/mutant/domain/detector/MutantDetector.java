package com.mercadolibre.mutant.domain.detector;

/**
 * Algoritmo optimizado de detección de mutantes
 * 
 * OPTIMIZACIONES CRÍTICAS IMPLEMENTADAS:
 * 1. Conversión inmediata a char[][] (evita overhead de String)
 * 2. Early Termination: Detiene búsqueda al encontrar >1 secuencia
 * 3. Exploración direccional optimizada (solo 4 direcciones necesarias)
 * 4. Sin recursión (evita overhead de stack)
 * 5. Complejidad: O(N²) worst case, ~O(N) average case para mutantes
 * 
 * DIRECCIONES DE BÚSQUEDA:
 * - Horizontal: → (derecha)
 * - Vertical: ↓ (abajo)
 * - Diagonal principal: ↘ (derecha-abajo)
 * - Diagonal secundaria: ↙ (izquierda-abajo)
 * 
 * No es necesario buscar en direcciones opuestas (←, ↑, ↖, ↗) 
 * ya que se detectarían en el recorrido normal
 */
public class MutantDetector {

    private static final int SEQUENCE_LENGTH = 4;
    private static final int MIN_SEQUENCES_FOR_MUTANT = 2;

    /**
     * Detecta si una secuencia de ADN pertenece a un mutante
     * 
     * @param dna Array de strings representando cada fila de la matriz de ADN
     * @return true si es mutante (>1 secuencia de 4 letras iguales), false en caso contrario
     * @throws IllegalArgumentException si el ADN es inválido
     */
    public boolean isMutant(String[] dna) {
        validateDna(dna);
        
        final int n = dna.length;
        final char[][] matrix = convertToCharMatrix(dna, n);
        
        return findMutantSequences(matrix, n) >= MIN_SEQUENCES_FOR_MUTANT;
    }

    /**
     * Valida que la secuencia de ADN sea válida:
     * - No nula ni vacía
     * - Matriz NxN (cuadrada)
     * - Solo contiene caracteres válidos (A, T, C, G)
     */
    private void validateDna(String[] dna) {
        if (dna == null || dna.length == 0) {
            throw new IllegalArgumentException("DNA sequence cannot be null or empty");
        }

        final int n = dna.length;
        
        // Validación de matriz cuadrada y caracteres válidos
        for (String sequence : dna) {
            if (sequence == null || sequence.length() != n) {
                throw new IllegalArgumentException("DNA must be an NxN matrix");
            }
            
            // Validación de caracteres (A, T, C, G)
            for (int i = 0; i < sequence.length(); i++) {
                char c = sequence.charAt(i);
                if (c != 'A' && c != 'T' && c != 'C' && c != 'G') {
                    throw new IllegalArgumentException("DNA must contain only A, T, C, G characters");
                }
            }
        }
    }

    /**
     * Convierte String[] a char[][] para acceso más rápido
     * Performance: char[][] es ~2-3x más rápido que String.charAt()
     */
    private char[][] convertToCharMatrix(String[] dna, int n) {
        char[][] matrix = new char[n][n];
        for (int i = 0; i < n; i++) {
            matrix[i] = dna[i].toCharArray();
        }
        return matrix;
    }

    /**
     * Encuentra secuencias de mutante con Early Termination
     * Detiene la búsqueda inmediatamente al encontrar 2 secuencias
     * 
     * @param matrix Matriz de caracteres del ADN
     * @param n Tamaño de la matriz
     * @return Número de secuencias encontradas (máximo 2 por early termination)
     */
    private int findMutantSequences(char[][] matrix, int n) {
        int sequencesFound = 0;

        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                // CRITICAL: Early Termination
                if (sequencesFound >= MIN_SEQUENCES_FOR_MUTANT) {
                    return sequencesFound;
                }

                char currentChar = matrix[row][col];

                // Horizontal → (solo si hay espacio suficiente)
                if (col <= n - SEQUENCE_LENGTH) {
                    if (checkSequence(matrix, row, col, 0, 1, currentChar)) {
                        sequencesFound++;
                        if (sequencesFound >= MIN_SEQUENCES_FOR_MUTANT) {
                            return sequencesFound;
                        }
                    }
                }

                // Vertical ↓ (solo si hay espacio suficiente)
                if (row <= n - SEQUENCE_LENGTH) {
                    if (checkSequence(matrix, row, col, 1, 0, currentChar)) {
                        sequencesFound++;
                        if (sequencesFound >= MIN_SEQUENCES_FOR_MUTANT) {
                            return sequencesFound;
                        }
                    }

                    // Diagonal principal ↘ (solo si hay espacio suficiente)
                    if (col <= n - SEQUENCE_LENGTH) {
                        if (checkSequence(matrix, row, col, 1, 1, currentChar)) {
                            sequencesFound++;
                            if (sequencesFound >= MIN_SEQUENCES_FOR_MUTANT) {
                                return sequencesFound;
                            }
                        }
                    }

                    // Diagonal secundaria ↙ (solo si hay espacio suficiente)
                    if (col >= SEQUENCE_LENGTH - 1) {
                        if (checkSequence(matrix, row, col, 1, -1, currentChar)) {
                            sequencesFound++;
                            if (sequencesFound >= MIN_SEQUENCES_FOR_MUTANT) {
                                return sequencesFound;
                            }
                        }
                    }
                }
            }
        }

        return sequencesFound;
    }

    /**
     * Verifica si existe una secuencia de 4 caracteres iguales en una dirección específica
     * Optimizado con loop unrolling manual para máxima velocidad
     * 
     * @param matrix Matriz de ADN
     * @param startRow Fila inicial
     * @param startCol Columna inicial
     * @param rowDelta Incremento de fila (-1, 0, 1)
     * @param colDelta Incremento de columna (-1, 0, 1)
     * @param expectedChar Carácter esperado
     * @return true si se encuentra la secuencia completa
     */
    private boolean checkSequence(char[][] matrix, int startRow, int startCol, 
                                   int rowDelta, int colDelta, char expectedChar) {
        // Loop unrolling: Más rápido que un bucle for
        // Verifica posiciones 1, 2, 3 (la 0 ya fue validada)
        return matrix[startRow + rowDelta][startCol + colDelta] == expectedChar &&
               matrix[startRow + 2 * rowDelta][startCol + 2 * colDelta] == expectedChar &&
               matrix[startRow + 3 * rowDelta][startCol + 3 * colDelta] == expectedChar;
    }
}
