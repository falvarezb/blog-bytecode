package fjab.graphs.api

import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, Set}

/**
  * This trait extends GraphTraversal to modify `findPath` in such a way that different paths
  * share knowledge of vertices already visited
  */
trait SharedKnowledgeGraphTraversal[T] extends GraphTraversal[T]{

  override def findPath(from: Seq[Path]): Path = {

    val paths: ListBuffer[Path] = ListBuffer() ++= from
    val exploredVertices: Set[Vertex] = mutable.Set(from.flatten: _*)

    def next(): Path = paths.headOption match{
      case None => Nil
      case Some(currentVertexPath) =>
        if(isSolution(currentVertexPath)) currentVertexPath.reverse
        else {
          val currentVertex = currentVertexPath.head
          //.filterNot(exploredVertices.contains(_)) is less generic as it is not valid for negative binary or optimal change
          val neighbourVertices = neighbours(currentVertex).filterNot(exploredVertices.contains(_)).filter(isVertexEligibleForPath(_, currentVertexPath))
          val pathsToNeighbourVertices = neighbourVertices.map(_ :: currentVertexPath)
          paths.remove(0)
          addNeighbours(paths, pathsToNeighbourVertices)
          exploredVertices ++= neighbourVertices
          next()
        }
    }

    next()

  }

}

