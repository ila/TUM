library(pacman)

pacman::p_load("ggplot2", "GGally", "corrplot", "scales", "gridExtra", "lubridate",
               "grid", "ggpubr", "RColorBrewer", "dplyr", "ggforce", "waffle", 
               "reshape2", "cluster", "fpc", "NbClust", "data.table", "network", 'here', 
               'magrittr', 'dplyr')



# Loading data

dt <- 'C:/Users/Mae/Desktop/axel'
setwd(dt)

# create .here()
airports  <- fread('airports.csv', header = T)
airlines <- fread('airlines.csv', header = T)
flights  <- fread("flights.csv", header = T)

flights_samp <- sample_n(flights, nrow(flights)*0.01)


# Preparing the data

general_data <- 
  flights_samp %>% 
  inner_join(
    airports %>% 
      select(IATA_CODE,  LATITUDE,  LONGITUDE) %>% 
      set_names(c('air_orig', 'lat_orig', 'long_orig')),
    by = c('ORIGIN_AIRPORT' = 'air_orig')
  ) %>% 
  left_join(
    airports %>% 
      select(IATA_CODE,  LATITUDE,  LONGITUDE) %>% 
      set_names(c('air_dest', 'lat_dest', 'long_dest')),
    by = c('DESTINATION_AIRPORT' = 'air_dest')
  ) %>% 
  select(YEAR, AIRLINE, FLIGHT_NUMBER, ORIGIN_AIRPORT, 
         DESTINATION_AIRPORT, lat_orig, long_orig, lat_dest, long_dest)

general_flights_orig <- 
  general_data %>% 
  group_by(ORIGIN_AIRPORT, DESTINATION_AIRPORT) %>% 
  summarise(flights_orig = n()) %>% 
  ungroup() %>% 
  select(ORIGIN_AIRPORT, DESTINATION_AIRPORT, flights_orig) %>% 
  arrange(ORIGIN_AIRPORT, DESTINATION_AIRPORT)

general_flights_dest <- 
  general_data %>% 
  group_by(DESTINATION_AIRPORT, ORIGIN_AIRPORT) %>% 
  summarise(flights_dest = n()) %>% 
  ungroup() %>% 
  select(ORIGIN_AIRPORT, DESTINATION_AIRPORT, flights_dest) %>% 
  arrange(DESTINATION_AIRPORT, ORIGIN_AIRPORT)



# PLOTS

library(igraph)

nodes_prev <- 
  general_data %>% 
  select(ORIGIN_AIRPORT) %>% 
  unique() %>% 
  set_names("airports") %>% 
  rbind(general_data %>% 
          select(DESTINATION_AIRPORT) %>% 
          unique() %>% 
          set_names("airports")) %>% 
  arrange(airports) %>% 
  unique()
  
links_prev <- 
  general_flights_orig %>% 
  left_join(general_flights_dest,
            by = c("ORIGIN_AIRPORT" = "DESTINATION_AIRPORT",
                   "DESTINATION_AIRPORT" = "ORIGIN_AIRPORT")) %>% 
  set_names(c("departure_airport", "arrival_airport", "num_departures", "num_arrivals"))


nodes <- 
  nodes_prev %>% 
  left_join(
    links_prev %>% 
    replace(., is.na(.), 0) %>% 
    mutate(total = num_departures + num_arrivals) %>% 
    group_by(departure_airport) %>% 
    summarise(total_airport = sum(total)) %>% 
    ungroup(),
    by = c("airports" = "departure_airport")
  ) %>% 
  set_names(c("id", "total")) %>% 
  mutate(
    total = if_else(is.na(total), 0, total),
    max_total = max(total, na.rm = T),
    min_total = min(total, na.rm = T),
    total_stn_1 = (max_total - total)/(max_total - min_total),
    total_stn_2 = total/(max_total)*30
  )

links <- 
  links_prev %>% 
  select(departure_airport, arrival_airport, num_departures) %>% 
  set_names(c("from", "to", "flights"))


rownames(links) <- NULL

net <- graph_from_data_frame(d=links, vertices=nodes, directed=T) 
class(net)


as_data_frame(net, what="edges")
as_data_frame(net, what="vertices")


V(net)$size <- V(net)$total_stn_2
V(net)$label.color <- "black"
V(net)$media <- V(net)$name

E(net)$width <-E(net)$flights/6
E(net)$arrow.size <- .2


plot(net, vertex.shape="none", 
     vertex.label=V(net)$media,vertex.label.font=2, vertex.label.color="gray40",
     vertex.label.cex=.7, edge.color="gray85")

tkplot(net)

l <- layout.fruchterman.reingold(net)
plot(net, layout=layout.fruchterman.reingold) 
plot(net, layout=l)
