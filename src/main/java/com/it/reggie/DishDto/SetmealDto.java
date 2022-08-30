package com.it.reggie.DishDto;

import com.it.reggie.entity.Setmeal;
import com.it.reggie.entity.SetmealDish;
import com.it.reggie.entity.Setmeal;
import com.it.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
