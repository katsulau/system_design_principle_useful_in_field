package chapter2.division

fun main() {
    val feeType: FeeType = FeeType.valueOf("ADULT")
    println("${feeType.label()}の料金は${feeType.yen().getValue()}円です")
}