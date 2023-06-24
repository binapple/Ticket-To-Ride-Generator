import {Pipe, PipeTransform} from "@angular/core";
import {City} from "../../dtos/city";

@Pipe({name: 'searchCity',
        pure:false})
export class SearchCityPipe implements PipeTransform {
  transform(list: any[], city: City): any {

    let returnList: any[] = list;

    if(city.name !== '')
    {
      returnList = returnList ? returnList.filter(item => item.name.search(new RegExp(city.name, 'i')) > -1) : [];
    }

    return returnList;

  }
}
