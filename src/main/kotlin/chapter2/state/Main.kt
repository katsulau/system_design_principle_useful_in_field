package chapter2.state

fun main() {
    val stateTransitions = StateTransitions()

    // 審査中から承認済は遷移できる
    println(stateTransitions.canTransit(StateType.審査中, StateType.承認済))

    // 実施中から差し戻し中には遷移できない
    println(stateTransitions.canTransit(StateType.実施中, StateType.差し戻し中))

}