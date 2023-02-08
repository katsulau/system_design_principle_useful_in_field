package chapter2.state

import java.util.EnumSet

class StateTransitions{

    private val allowed: Map<StateType, Set<StateType>>
    init {
        val hashMap = HashMap<StateType, Set<StateType>>()
        hashMap[StateType.審査中] = EnumSet.of(StateType.承認済, StateType.差し戻し中)
        hashMap[StateType.差し戻し中] = EnumSet.of(StateType.審査中, StateType.終了)
        hashMap[StateType.承認済] = EnumSet.of(StateType.実施中, StateType.終了)
        hashMap[StateType.実施中] = EnumSet.of(StateType.中断中, StateType.終了)
        hashMap[StateType.中断中] = EnumSet.of(StateType.実施中, StateType.終了)
        this.allowed = hashMap
    }

    fun canTransit(from: StateType, to: StateType): Boolean {
        val allowedStates = allowed[from]
            ?: throw IllegalArgumentException("from: ${from}から遷移可能な状態は存在しません")
        return allowedStates.contains(to)
    }
}