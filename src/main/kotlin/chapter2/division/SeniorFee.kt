package chapter2

class SeniorFee: Fee {
    override fun yen(): Yen {
        return Yen(70)
    }

    override fun label(): String {
        return "シニア"
    }
}