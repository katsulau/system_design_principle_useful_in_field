package chapter2.division

class SeniorFee: Fee {
    override fun yen(): Yen {
        return Yen(70)
    }

    override fun label(): String {
        return "シニア"
    }
}