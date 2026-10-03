package com.wendev.kolas.data.ml

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DogLabelsTest {

    @Test
    fun `generic dog label is a dog`() {
        assertTrue(DogLabels.isDog("Dog"))
    }

    @Test
    fun `breed labels are dogs`() {
        assertTrue(DogLabels.isDog("Shetland sheepdog"))
        assertTrue(DogLabels.isDog("Basset hound"))
        assertTrue(DogLabels.isDog("Cairn terrier"))
        assertTrue(DogLabels.isDog("Dalmatian"))
    }

    @Test
    fun `hot dog is not a dog`() {
        assertFalse(DogLabels.isDog("Hot dog"))
    }

    @Test
    fun `human and other labels are not dogs`() {
        assertFalse(DogLabels.isDog("Person"))
        assertFalse(DogLabels.isDog("Face"))
        assertFalse(DogLabels.isDog("Skin"))
        assertFalse(DogLabels.isDog("Cat"))
    }

    @Test
    fun `blank label is not a dog`() {
        assertFalse(DogLabels.isDog(""))
    }
}
