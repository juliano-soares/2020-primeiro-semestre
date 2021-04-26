#ifndef PATHFINDER_H
#define PATHFINDER_H

#include <vector>
#include "NavigationNode.h"

struct Path {
  NavigationNode* start;
  NavigationNode* end;
  enum {UNPROCCESSED, FOUND, IMPOSSIBLE} status;
}

class Pathfinder {
  std:vector<NavigationNode*> openList;
  std:vector<NavigationNode*> closedList;
  
  inline win32 calculateHCost(const NavigationNode* current, const NavigationNode* end) const {
    win16 absX = ABS(current->x - end->x);
    win16 absY = ABS(current->y - end->y);
    win32 h;

    if (absX>absY) {
      h= 14*absY+10*(absX-absY)
    }
  }

public:
  Pathfinder(win32 size) : openList(size), closedList(size) {}  
  
  bool FindPath(NavigationNode* start, NavigationNode* end) {
    Path path;
    path.start = start;
    path.end = end;
    path.status = Path::UNPROCCESSED;

    // Iniciliza o no start
    start->FCost = start->GCost = 0;
    start->HCost = 
    start->parent = nullptr;

    return path;
  }
};

#endif
