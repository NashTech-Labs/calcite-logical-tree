package com.knoldus.calcite.util

import org.apache.calcite.plan.volcano.{RelSubset, VolcanoPlanner}
import org.apache.calcite.plan.{Convention, ConventionTraitDef, RelOptCost}
import org.apache.calcite.rel.RelNode
import org.apache.calcite.rel.metadata.RelMetadataQuery

class CalciteVolcanoPlanner extends VolcanoPlanner {

  override def getCost(rel: RelNode, mq: RelMetadataQuery): RelOptCost = {
    assert(rel != null, "pre-condition: rel != null")
    if (rel.isInstanceOf[RelSubset]) try {
      val field = classOf[RelSubset].getDeclaredField("bestCost")
      field.setAccessible(true)
      return field.get(rel).asInstanceOf[RelOptCost]
    } catch {
      case e: Exception =>
        e.printStackTrace()
        throw new RuntimeException("this is a test")
    }
    if (rel.getTraitSet.getTrait(ConventionTraitDef.INSTANCE) == Convention.NONE)
      return costFactory.makeInfiniteCost()

    val zeroCost =
      try {
        val zero = classOf[VolcanoPlanner].getDeclaredField("zeroCost")
        zero.setAccessible(true)
        zero.get(this).asInstanceOf[RelOptCost]
      } catch {
        case e: Exception =>
          e.printStackTrace()
          return null
      }

    var cost = rel.computeSelfCost(this, mq)

    if (!zeroCost.isLt(cost)) { // cost must be positive, so nudge it
      cost = costFactory.makeTinyCost
    }

    rel.getInputs.forEach { input => cost = cost.plus(getCost(input, mq)) }

    cost
  }
}
