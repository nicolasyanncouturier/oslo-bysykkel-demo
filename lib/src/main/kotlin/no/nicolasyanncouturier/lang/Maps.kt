package no.nicolasyanncouturier.lang

class Maps {
    companion object {
        @JvmStatic fun <K, V1, V2> leftJoin(left: Map<K, V1>, right: Map<K, V2>): Map<K, Pair<V1, V2>> {
            return left.entries
                    .mapNotNull { entry -> right[entry.key]?.let { entry.key to (entry.value to it) } }
                    .toMap()
        }
    }
}