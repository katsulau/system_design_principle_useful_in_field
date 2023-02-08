package chapter2.division

class AdultFee: Fee {
    override fun yen(): Yen {
        return Yen(100)
    }

    override fun label(): String {
        return "大人"
    }
}