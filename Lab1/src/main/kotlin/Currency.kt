package org.example

class Currency(
    var amount : Int,
    val factor : Int
) {
    override fun toString(): String {
        // TODO:
        //  Could be division error
        //  But it's only in printing
        return (amount.toDouble() / factor).toString()
    }
}