package io.github.isakpedersen.fishinglogger.sync

import io.github.isakpedersen.fishinglogger.data.LureModel
import io.github.isakpedersen.fishinglogger.data.LureType
import io.github.isakpedersen.fishinglogger.data.LureVariant
import io.github.isakpedersen.fishinglogger.data.Rig
import org.junit.Assert.assertEquals
import org.junit.Test

class LureCatalogTreeTest {
    @Test
    fun `builds brand, model and color levels with ids on the leaves`() {
        val models = listOf(
            LureModel(id = 1, type = LureType.SLUK, name = "Møresilda", brand = "Remen"),
            LureModel(id = 2, type = LureType.SLUK, name = "Spesial Classic", brand = "Sølvkroken"),
            LureModel(id = 3, type = LureType.SLUK, name = "Kulpkrokodill", brand = null),
            LureModel(id = 4, type = LureType.MARK, name = "Mark", brand = null),
        )
        val variants = listOf(
            LureVariant(id = 1, lureModelId = 1, color = "C/R", weight = 10.0, length = null),
            LureVariant(id = 2, lureModelId = 1, color = "C/R", weight = 15.0, length = null),
            LureVariant(id = 3, lureModelId = 2, color = "S/D", weight = 7.0, length = null),
            LureVariant(id = 4, lureModelId = 2, color = "C/R", weight = 7.0, length = null),
            LureVariant(id = 5, lureModelId = 3, color = null, weight = 15.0, length = null),
            LureVariant(id = 6, lureModelId = 4, color = null, weight = null, length = null),
        )

        // @formatter:off
        val expected = listOf(
            Node("Sluk", listOf(
                Node("Remen", listOf(
                    Node("Møresilda", listOf(
                        Node("C/R", listOf(
                            Leaf("10 g", id = 1, type = LureType.SLUK, brand = "Remen", name = "Møresilda", color = "C/R"),
                            Leaf("15 g", id = 2, type = LureType.SLUK, brand = "Remen", name = "Møresilda", color = "C/R")
                        ))
                    ))
                )),
                Node("Sølvkroken", listOf(
                    Node("Spesial Classic", listOf(
                        Node("S/D", listOf(
                            Leaf("7 g", id = 3, type = LureType.SLUK, brand = "Sølvkroken", name = "Spesial Classic", color = "S/D")
                        )),
                        Node("C/R", listOf(
                            Leaf("7 g", id = 4, type = LureType.SLUK, brand = "Sølvkroken", name = "Spesial Classic", color = "C/R")
                        ))
                    ))
                )),
                Node("Uten merke", listOf(
                    Node("Kulpkrokodill", listOf(
                        Node("Ukjent farge", listOf(
                            Leaf("15 g", id = 5, type = LureType.SLUK, brand = null, name = "Kulpkrokodill", color = null)
                        ))
                    ))
                ))
            )),
            Node("Mark", listOf(
                Node("Uten merke", listOf(
                    Node("Mark", listOf(
                        Node("Ukjent farge", listOf(
                            Leaf("Ukjent vekt/lengde", id = 6, type = LureType.MARK, brand = null, name = "Mark", color = null, rigs = listOf(Rig.OPPHENG, Rig.BUNNMEITE))
                        ))
                    ))
                ))
            ))
        )
        // @formatter:on

        assertEquals(expected, composeLureCatalog(models, variants))
    }

    @Test
    fun `empty catalog in produces empty catalog out`() {
        val models = emptyList<LureModel>()
        val variants = emptyList<LureVariant>()

        val expected = emptyList<CatalogItem>()

        assertEquals(expected, composeLureCatalog(models, variants))
    }

    @Test
    fun `a model with no variants is omitted from the tree`() {
        val models = listOf(
            LureModel(id = 1, type = LureType.SLUK, name = "Møresilda", brand = "Remen"),
            LureModel(id = 2, type = LureType.SLUK, name = "Spesial Classic", brand = "Sølvkroken"),
        )
        val variants = listOf(
            LureVariant(id = 1, lureModelId = 1, color = "C/R", weight = 10.0, length = null),
        )

        // @formatter:off
        val expected = listOf(
            Node("Sluk", listOf(
                Node("Remen", listOf(
                    Node("Møresilda", listOf(
                        Node("C/R", listOf(
                            Leaf("10 g", id = 1, type = LureType.SLUK, brand = "Remen", name = "Møresilda", color = "C/R")
                        ))
                    ))
                ))
            ))
        )
        // @formatter:on

        assertEquals(expected, composeLureCatalog(models, variants))
    }

    @Test
    fun `leaf labels show formatted decimal weight when present, otherwise length`() {
        val models = listOf(
            LureModel(id = 1, type = LureType.SLUK, name = "Møresilda", brand = "Remen"),
        )
        val variants = listOf(
            LureVariant(id = 1, lureModelId = 1, color = "C/R", weight = 10.5, length = null),
            LureVariant(id = 2, lureModelId = 1, color = "C/R", weight = null, length = 7.0),
            LureVariant(id = 3, lureModelId = 1, color = "C/R", weight = 10.0, length = 7.5),
        )

        // @formatter:off
        val expected = listOf(
            Node("Sluk", listOf(
                Node("Remen", listOf(
                    Node("Møresilda", listOf(
                        Node("C/R", listOf(
                            Leaf("10.5 g", id = 1, type = LureType.SLUK, brand = "Remen", name = "Møresilda", color = "C/R"),
                            Leaf("7 cm", id = 2, type = LureType.SLUK, brand = "Remen", name = "Møresilda", color = "C/R"),
                            Leaf("10 g", id = 3, type = LureType.SLUK, brand = "Remen", name = "Møresilda", color = "C/R"),
                        ))
                    ))
                ))
            ))
        )
        // @formatter:on

        assertEquals(expected, composeLureCatalog(models, variants))
    }

    @Test
    fun `toWire converts a nested tree to nested maps`() {
        // @formatter:off
        val tree = Node("Sluk", listOf(
                Node("Sølvkroken", listOf(
                    Node("Spesial Classic", listOf(
                        Node("Sølv", listOf(
                            Leaf("12 g", id = 1, type = LureType.SLUK, brand = "Sølvkroken", name = "Spesial Classic", color = "Sølv"),
                            Leaf("15 g", id = 2, type = LureType.SLUK, brand = "Sølvkroken", name = "Spesial Classic", color = "Sølv"),
                        ))
                    ))
                ))
            ))
        // @formatter:on

        // @formatter:off
        val expected = mapOf("label" to "Sluk", "items" to listOf(
            mapOf("label" to "Sølvkroken", "items" to listOf(
                mapOf("label" to "Spesial Classic", "items" to listOf(
                    mapOf("label" to "Sølv", "items" to listOf(
                        mapOf("label" to "12 g", "id" to 1, "type" to "Sluk", "brand" to "Sølvkroken", "name" to "Spesial Classic", "color" to "Sølv"),
                        mapOf("label" to "15 g", "id" to 2, "type" to "Sluk", "brand" to "Sølvkroken", "name" to "Spesial Classic", "color" to "Sølv"),
                    ))
                ))
            ))
        ))
        // @formatter:on

        assertEquals(expected, tree.toWire())
    }

    @Test
    fun `toWire serializes rigs as wire names on leaves with rigs`() {
        val leaf = Leaf(
            label = "Mark",
            id = 1,
            type = LureType.MARK,
            name = "Mark",
            rigs = listOf(Rig.OPPHENG, Rig.BUNNMEITE),
        )

        val expected =
            mapOf(
                "label" to "Mark",
                "id" to 1,
                "type" to "Mark",
                "name" to "Mark",
                "rigs" to listOf("Oppheng", "Bunnmeite"),
            )

        val result = leaf.toWire()

        assertEquals(expected, result)
    }

    @Test
    fun `toWire omits rigs key on leaves with no rigs`() {
        val leaf = Leaf(
            label = "12 g",
            id = 1,
            type = LureType.SLUK,
            name = "Møresilda",
            rigs = emptyList(),
        )

        val expected = mapOf(
            "label" to "12 g",
            "type" to "Sluk",
            "name" to "Møresilda",
            "id" to 1,
        )

        val result = leaf.toWire()

        assertEquals(expected, result)
    }
}
