package thread

import org.objectweb.asm.tree.AbstractInsnNode

/**
 * Created by geyan01 on 2/26/22
 */
const val THREAD = "java/lang/Thread"
const val SHADOW_THREAD = "thread/ShadowThread"

// 从当前节点开始进行遍历，满足条件的话，返回对应的InsnNode
fun AbstractInsnNode.find(block: (AbstractInsnNode) -> Boolean): AbstractInsnNode? {
    var next: AbstractInsnNode? = this
    while (next != null) {
        if (block(next)) {
            return next
        }
        next = next.next
    }
    return null
}