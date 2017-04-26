/**
 * iita-common-web.struts Jul 7, 2010
 */
package org.iita.service.impl;

import org.testng.annotations.Test;

/**
 * @author mobreza
 */
@Test(suiteName = "Haha")
public class Query {
	@Test(groups = { "queryService" }, testName = "Check handling of aggregate having clause")
	public void countQueryHaving() {
		QueryServiceImpl queryService = new QueryServiceImpl();
		String x;
		x= queryService
				.getCountQuery("select item.name, count(lot.id), sum(lot.quantity) as qty from Lot lot inner join lot.item item group by lot.item having sum(lot.quantity)=0");
		assert x.equalsIgnoreCase("select count(distinct lot.item) from Lot lot inner join lot.item item group by lot.item having sum(lot.quantity)=0") : "Failed with aggregate having clause";
		x = queryService
				.getCountQuery("select l.id, sum(qu.quantity), qu.description.transactionType, 'Contamination or Necrosis' from QuantityUpdate qu join qu.lot l where qu.description.status>0 and qu.description.transactionType=0 and qu.description.transactionSubtype in ('NECROSIS', 'CONTAMINATION') group by l.id, qu.description.transactionType");
		assert x.equalsIgnoreCase("select count(distinct l.id) from QuantityUpdate qu join qu.lot l where qu.description.status>0 and qu.description.transactionType=0 and qu.description.transactionSubtype in ('NECROSIS', 'CONTAMINATION') group by  qu.description.transactionType") : "Failed with normal grouping";
	}
}
