window.addEventListener("load", function() {
  // Wait until Swagger UI is initialized
  if (window.ui) {
    window.ui.getConfigs().operationsSorter = function(a, b) {
      var order = { GET: 1, POST: 2, PUT: 3, PATCH: 4, DELETE: 99 };
      return (order[a.get("method")] || 50) - (order[b.get("method")] || 50);
    };
  }
});
