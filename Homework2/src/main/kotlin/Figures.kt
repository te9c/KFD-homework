package org.example

sealed class Figures(val property : Double) {
    init {
        if (property <= 0 || property.isNaN()) {
            throw BadPropertyException("Property is less or equals to 0 or NaN")
        }
        println(this)
    }

    override fun toString() = "${this::class.simpleName}(property=$property)"
}

class Square(property : Double) : Figures(property)
class Circle(property : Double) : Figures(property)