import Socket from "../util/Socket";
import { movieEPs } from "../Config.json";

const { searchEP, browseEP, getEP } = movieEPs;
const { thumbnailEP} = movieEPs


async function search(paramQuery) {
    var url = searchEP;
    url += paramQuery;
    console.log(url);
    return await Socket.GET(url);
  };

  async function browse(paramQuery) {
    var url = browseEP;
    url += paramQuery;
    console.log(url);
    return await Socket.GET(url);
  };

async function thumbnail(movie_list) {
  const payLoad = {
    movie_ids: movie_list
  }
  return await Socket.POST(thumbnailEP, payLoad);
}

async function get(movie_id) {
  var url = getEP;
    url += movie_id;
    console.log("request Get: " + url);
    return await Socket.GET(url);
  };

export default {search, browse, thumbnail, get}