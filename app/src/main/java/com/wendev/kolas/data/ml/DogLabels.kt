package com.wendev.kolas.data.ml

/**
 * Maps ML Kit's base-model label text to "this is a dog".
 *
 * The base model emits 400+ labels; the dog-related ones are the generic "Dog"
 * plus a handful of breed labels. "Hot dog" is deliberately excluded. Kept free
 * of Android/ML Kit types so it can be covered by a plain JVM unit test.
 */
object DogLabels {

    private val DOG_LABELS: Set<String> = setOf(
        "dog",
        "shetland sheepdog",
        "basset hound",
        "cairn terrier",
        "boxer",
        "dalmatian",
        "cavalier"
    )

    /** True when [label] names a dog (case-insensitive, trimmed). */
    fun isDog(label: String): Boolean = label.trim().lowercase() in DOG_LABELS
}
