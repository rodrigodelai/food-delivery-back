package com.delai.model.order;

import java.math.BigDecimal;
import java.util.List;

import com.delai.model.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private BigDecimal price;
	private Integer quantity;
	private String notes;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private Product product;
	
	@ManyToMany(fetch = FetchType.EAGER)
	private List<OrderOptionsList> orderOptionsLists;
	
}
