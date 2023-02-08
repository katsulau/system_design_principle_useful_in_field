package chapter2.division

class ChildFee: Fee {
    override fun yen(): Yen {
        return Yen(50)
    }

    override fun label(): String {
        return "子供"
    }
}