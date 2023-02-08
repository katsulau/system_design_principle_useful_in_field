package chapter2

enum class FeeType(
    private val fee: Fee
) {
    ADULT(AdultFee()),
    CHILD(ChildFee()),
    SENIOR(SeniorFee());

    fun yen(): Yen {
        return this.fee.yen()
    }

    fun label(): String {
        return this.fee.label()
    }
}