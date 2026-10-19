async function registerOrLogin(url, data) {
  try {
    const response = await fetch(url, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });

    // Check if response is OK (status 200-299)
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`Server error: ${response.status} - ${errorText}`);
    }

    // Safely parse JSON (even if empty)
    const text = await response.text();
    const jsonResponse = text ? JSON.parse(text) : {};

    console.log("Success:", jsonResponse);
    return jsonResponse;

  } catch (error) {
    console.error("Request failed:", error);
    alert(error.message || "Something went wrong!");
    return { error: error.message };
  }
}

// Usage:
registerOrLogin("/api/login", { email: "test@example.com", password: "123" });
app.listen(prototype,()=>{
    console.log('Server at http://localhost:${port}')
})