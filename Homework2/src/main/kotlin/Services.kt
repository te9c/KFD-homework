package org.example

import kotlin.math.PI

interface ConsoleService {
    fun work()
}

interface FigureService {
    fun addSquare(property: Double)
    fun addCircle(property: Double)
    fun getPerimeter() : Double
    fun getArea() : Double
}

class FigureServiceImpl : FigureService {
    private var totalArea: Double = 0.0
    private var totalPerimeter: Double = 0.0
    private var figureList: MutableList<Figures> = ArrayList<Figures>()

    override fun addSquare(property: Double) {
        figureList.add(Square(property))
        totalArea += property * property
        totalPerimeter += property * 4
    }
    override fun addCircle(property: Double) {
        figureList.add(Circle(property))
        totalArea += PI * property * property
        totalPerimeter += 2 * PI * property
    }
    override fun getPerimeter(): Double {
        return totalPerimeter
    }
    override fun getArea(): Double {
        return totalArea
    }
}

class ConsoleServiceImpl(val figureService: FigureService = FigureServiceImpl()) : ConsoleService {
    private fun getOperation(str : String) : Operation {
        return when (str) {
            "1" -> Operation.INSERT
            "2" -> Operation.GET_AREA
            "3" -> Operation.GET_PERIMETER
            "4" -> Operation.EXIT
            else -> throw WrongOperationTypeException("Введён неизвестный тип операции: $str")
        }
    }
    private fun getFigureChoice(str : String) : FigureChoice {
        return when (str) {
            "1" -> FigureChoice.CIRCLE
            "2" -> FigureChoice.SQUARE
            else -> throw WrongFigureChoiceException("Введен неизвестный тип фигуры: $str")
        }
    }
    private fun getArea() {
        println("Площадь: ${figureService.getArea()}")
    }
    private fun getPerimeter() {
        println("Периметр: ${figureService.getPerimeter()}")
    }

    private fun addFigure() {
        println("""Введите тип фигуры:
            |1) Круг
            |2) Квадрат
        """.trimMargin())
        val figureType = try {
            getFigureChoice(readln())
        } catch (e: WrongFigureChoiceException) {
            println(e.message)
            return
        }

        println("Введите property: ")
        val property = try {
            readln().toDouble()
        } catch (e: NumberFormatException) {
            println("Invalid format: ${e.toString()}")
            return
        }

        when (figureType) {
            FigureChoice.CIRCLE -> figureService.addCircle(property)
            FigureChoice.SQUARE -> figureService.addSquare(property)
        }
    }
    override fun work() {
        while(true) {
            println("""Введите тип операции, которую хотите исполнить:
                |1) добавить фигуру
                |2) получить площадь всех фигур
                |3) получить периметр всех фигур
                |4) завершить выполнение""".trimMargin())
            var operation = try {
                getOperation(readln())
            } catch (e: WrongOperationTypeException) {
                println(e.message)
                continue
            }
            when (operation) {
                Operation.INSERT -> addFigure()
                Operation.GET_AREA -> getArea()
                Operation.GET_PERIMETER -> getPerimeter()
                Operation.EXIT -> break
            }
        }
    }
}