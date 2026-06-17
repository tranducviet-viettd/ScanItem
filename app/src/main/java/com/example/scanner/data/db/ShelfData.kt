package com.example.scanner.data.db

import io.github.sceneview.math.Position
import java.util.PriorityQueue
import kotlin.math.sqrt

// 1. Định nghĩa các điểm (Node) trên lối đi
data class GraphNode(
    val id: String,
    val position: Position
)
// 2. Định nghĩa các đường nối (Edge) giữa các điểm
data class GraphEdge(
    val nodeA: String,
    val nodeB: String
)
data class Shelf(
    val name: String,
    val position: Position,
    val targetNodeId: String
)

object ShelfRepository {

    // Vị trí cửa vào
    val entrance = Position(x = 8f, y = 0f, z = 11f)

    val graphNodes = listOf(
        GraphNode("START", entrance),
        GraphNode("A1", Position(x = -11f, y = 1f, z = 11f)),
        GraphNode("A2", Position(x = -11f, y = 1f, z = 5f)),
        GraphNode("A3", Position(x = -11f, y = 1f, z = -1f)),
        GraphNode("A4", Position(x = -11f, y = 1f, z = -7f)),
        GraphNode("A5", Position(x = -11f, y = 1f, z = -13f)),
        GraphNode("A6", Position(x = -9f, y = 1f, z = -13f)),
        GraphNode("A7", Position(x = -6f, y = 1f, z = -13f)),
        GraphNode("A8", Position(x = -3.68f, y = 1f, z = -13f)),
        GraphNode("A9", Position(x = 0f, y = 1f, z = -13f)),
        GraphNode("A10", Position(x = 2.68f, y = 1f, z = -13f)),
        GraphNode("A11", Position(x = 6f, y = 1f, z = -13f)),
        GraphNode("A12", Position(x = 8.01f, y = 1f, z = -13f)),

        // ... THÊM CÁC NODE CỦA BẠN VÀO ĐÂY
    ).associateBy { it.id }
    // Khai báo các đường nối (aisles) giữa các Node (đường thẳng không bị vướng kệ)
    val graphEdges = listOf(

        GraphEdge("A1", "A2"),
        GraphEdge("A2", "A3"),
        GraphEdge("A3", "A4"),
        GraphEdge("A4", "A5"),
        GraphEdge("A5", "A6"),
        GraphEdge("A6", "A7"),
        GraphEdge("A7", "A8"),
        GraphEdge("A8", "A9"),
        GraphEdge("A9", "A10"),
        GraphEdge("A10", "A11"),
        GraphEdge("A11", "A12"),

        // Ví dụ: GraphEdge("A1", "A2"),
        // ... THÊM CÁC CẠNH CỦA BẠN VÀO ĐÂY
    )

    val shelves = listOf(
        Shelf("K1",  Position(-13f,   1f,  11f), "A1"),
        Shelf("K2",  Position(-9f,    1f,  11f),"A1"),
        Shelf("K3",  Position(-13f,   1f,   5f),"A2"),
        Shelf("K4",  Position(-9f,    1f,   5f),"A2"),
        Shelf("K5",  Position(-13f,   1f,  -1f),"A3"),
        Shelf("K6",  Position(-9f,    1f,  -1f),"A3"),
        Shelf("K7",  Position(-13f,   1f,  -7f),"A4"),
        Shelf("K8",  Position(-9f,    1f,  -7f),"A4"),
        Shelf("K9",  Position(-13f,   1f, -13f),"A5"),
        Shelf("K10", Position(-6f,    1f,  -9f),"A6"),
        Shelf("K11", Position(-9f,    1f, -14.5f),"A7"),
        Shelf("K12", Position( 0f,    1f,  -9f),"A8"),
        Shelf("K13", Position(-3.68f, 1f, -14.5f),"A9"),
        Shelf("K14", Position( 6f,    1f,  -9f),"A10"),
        Shelf("K15", Position( 2.09f, 1f, -14.5f),"A11"),
        Shelf("K17", Position( 8.01f, 1f, -14.5f),"A12"),
    )

    private val adjacencyList: Map<String, List<String>> by lazy {
        val map = mutableMapOf<String, MutableList<String>>()
        graphNodes.keys.forEach { map[it] = mutableListOf() }
        for (edge in graphEdges) {
            map[edge.nodeA]?.add(edge.nodeB)
            map[edge.nodeB]?.add(edge.nodeA) // Đồ thị vô hướng (đi được 2 chiều)
        }
        map
    }
    private fun heuristic(pos1: Position, pos2: Position): Float {
        val dx = pos1.x - pos2.x
        val dz = pos1.z - pos2.z
        return sqrt(dx * dx + dz * dz)
    }
    fun search(query: String): List<Shelf> {
        if (query.isBlank()) return emptyList()
        return shelves.filter { it.name.contains(query, ignoreCase = true) }
    }
    fun findByName(name: String): Shelf? {
        return shelves.find { it.name.equals(name, ignoreCase = true) }
    }
    // Thuật toán A* để tìm đường từ START đến targetNode của Shelf
    fun getPathTo(shelf: Shelf): List<Position> {
        val startId = "A1"
        val goalId = shelf.targetNodeId
        if (!graphNodes.containsKey(startId) || !graphNodes.containsKey(goalId)) {
            return emptyList()
        }
        val openSet = PriorityQueue<AStarNode>(compareBy { it.fScore })
        val cameFrom = mutableMapOf<String, String>()

        val gScore = mutableMapOf<String, Float>().withDefault { Float.POSITIVE_INFINITY }
        gScore[startId] = 0f
        val fScore = mutableMapOf<String, Float>().withDefault { Float.POSITIVE_INFINITY }
        fScore[startId] = heuristic(graphNodes[startId]!!.position, graphNodes[goalId]!!.position)
        openSet.add(AStarNode(startId, fScore[startId]!!))
        while (openSet.isNotEmpty()) {
            val currentId = openSet.poll()?.id ?: break
            if (currentId == goalId) {
                return reconstructPath(cameFrom, currentId, shelf.position)
            }
            val currentPos = graphNodes[currentId]!!.position
            for (neighborId in adjacencyList[currentId] ?: emptyList()) {
                val neighborPos = graphNodes[neighborId]!!.position
                val tentativeGScore = gScore.getValue(currentId) + heuristic(currentPos, neighborPos)
                if (tentativeGScore < gScore.getValue(neighborId)) {
                    cameFrom[neighborId] = currentId
                    gScore[neighborId] = tentativeGScore
                    val f = tentativeGScore + heuristic(neighborPos, graphNodes[goalId]!!.position)
                    fScore[neighborId] = f

                    // Nếu neighbor chưa có trong openSet thì thêm vào
                    if (openSet.none { it.id == neighborId }) {
                        openSet.add(AStarNode(neighborId, f))
                    }
                }
            }
        }
        // Không tìm thấy đường
        return emptyList()
    }
    private fun reconstructPath(cameFrom: Map<String, String>, currentId: String, finalTargetPosition: Position): List<Position> {
        val path = mutableListOf<Position>()
        var curr = currentId
        path.add(graphNodes[curr]!!.position)

        while (cameFrom.containsKey(curr)) {
            curr = cameFrom[curr]!!
            path.add(graphNodes[curr]!!.position)
        }
        path.reverse() // Vì truy vết ngược từ goal về start nên cần đảo lại

        // Thêm điểm chính xác của kệ hàng làm điểm cuối cùng
        path.add(finalTargetPosition)
        return path
    }
    // Lớp phụ trợ cho hàng đợi ưu tiên của A*
    private data class AStarNode(val id: String, val fScore: Float)
}
