package fjab.graphs.fjab.graphs.negativebinary

import fjab.graphs.Amount
import fjab.graphs.api.GraphTraversal

import scala.collection.mutable.ListBuffer

trait NegativeBinary extends GraphTraversal[Int]{

  self: Amount =>

  val moves: List[Int] = List(0, 1)

  override def neighbours(vertex: Vertex): List[Vertex] = moves

  override def addNeighbours(verticesToExplore: ListBuffer[Path], neighbours: List[Path]) =
    verticesToExplore ++= neighbours

  override def isSolution(path: Path): Boolean =
    path.reverse.zipWithIndex.foldLeft(0){case (acc, (vertex, idx)) => acc + vertex * BigInt(-2).pow(idx).toInt} == amount


  /**
    * By construction, a path can never visit the same vertex twice. Therefore, no extra filter is needed.
    */
  override def isVertexEligibleForPath(vertex: Vertex, path: Path): Boolean = true
}
