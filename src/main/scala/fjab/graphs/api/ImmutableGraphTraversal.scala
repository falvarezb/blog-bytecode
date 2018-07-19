package fjab.graphs.api

/**
  * This trait implements the method `findPath` making use of an immutable List to store
  * the vertices pending to visit
  */
trait ImmutableGraphTraversal[T] extends GraphTraversal[T]{

  override def findPath(seed: Seq[Path]): Path = {

    def next(paths: Seq[Path]): Path = paths match{
      case Nil => Nil
      case currentVertexPath :: rest =>
        if(isSolution(currentVertexPath)) currentVertexPath.reverse
        else {
          val currentVertex = currentVertexPath.head
          val neighbourVertices = neighbours(currentVertex).filter(isVertexEligibleForPath(_, currentVertexPath))
          val pathsToNeighbourVertices = neighbourVertices.map(_ :: currentVertexPath)
          next(addNeighbours(rest,pathsToNeighbourVertices))
        }
    }

    next(seed)

  }

  /**
    * This method adds the neighbours of the current vertex to the list of remaining vertices to explore and
    * determines the algorithm to traverse the graph, depth-first or breadth-first.
    *
    * Depending on whether the new paths are added in front of the list or at the end, the resulting traversing
    * algorithm will be depth-first or breadth-first respectively.
    *
    * Breadth-first algorithms are necessary to find the shortest path between 2 vertices or when looking
    * for paths in an infinite graph
    *
    */
  def addNeighbours(verticesToExplore: List[Path], neighbours: List[Path]): List[Path]

}

