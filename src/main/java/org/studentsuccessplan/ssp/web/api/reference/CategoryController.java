package org.studentsuccessplan.ssp.web.api.reference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.studentsuccessplan.ssp.model.reference.Category;
import org.studentsuccessplan.ssp.service.reference.CategoryService;
import org.studentsuccessplan.ssp.transferobject.reference.CategoryTO;

@PreAuthorize("hasRole('ROLE_USER')")
@Controller
@RequestMapping("/reference/category")
public class CategoryController extends
		AbstractAuditableReferenceController<Category, CategoryTO> {

	@Autowired
	protected CategoryController(final CategoryService service) {
		super(service, Category.class, CategoryTO.class);
	}
}