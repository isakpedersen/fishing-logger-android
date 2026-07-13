package io.github.isakpedersen.fishinglogger.sync

import io.github.isakpedersen.fishinglogger.data.LureModel
import io.github.isakpedersen.fishinglogger.data.LureType
import io.github.isakpedersen.fishinglogger.data.LureVariant
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

        val expected = listOf(
            Node("Sluk", listOf(
                Node("Remen", listOf(
                    Node("Møresilda", listOf(
                        Node("C/R", listOf(
                            Leaf("10 g", id = 1),
                            Leaf("15 g", id = 2)
                        ))
                    ))
                )),
                Node("Sølvkroken", listOf(
                    Node("Spesial Classic", listOf(
                        Node("S/D", listOf(
                            Leaf("7 g", id = 3)
                        )),
                        Node("C/R", listOf(
                            Leaf("7 g", id = 4)
                        ))
                    ))
                )),
                Node("Uten merke", listOf(
                    Node("Kulpkrokodill", listOf(
                        Node("Ukjent farge", listOf(
                            Leaf("15 g", id = 5)
                        ))
                    ))
                ))
            )),
            Node("Mark", listOf(
                Node("Uten merke", listOf(
                    Node("Mark", listOf(
                        Node("Ukjent farge", listOf(
                            Leaf("Ukjent vekt/lengde", id = 6)
                        ))
                    ))
                ))
            ))
        )

        assertEquals(expected, composeLureCatalog(models, variants))
    }
}