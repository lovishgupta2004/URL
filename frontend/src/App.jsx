import { useState } from 'react'
import './App.css'

function App() {
  const [originalUrl, setOriginalUrl] = useState('')
  const [customCode, setCustomCode] = useState('')
  const [error, setError] = useState('')
  const [shortUrl, setShortUrl] = useState('')

  const handleSubmit = async (e) => {
    e.preventDefault()

  setError('')

  // 1. Check if URL is empty
  if (!originalUrl.trim()) {
    setError('Please enter a URL')
    return
  }

  try {
    // 2. Send URL to Spring Boot backend
    const response = await fetch('http://localhost:8080/url', {
      method: 'POST',
      headers: {
       'Content-Type': 'text/plain',
      },
      body: originalUrl,
    })

    // 3. Check if backend returned an error
    if (!response.ok) {
      throw new Error('Failed to shorten URL')
    }

    // 4. Get short code returned by backend
    const shortCode = await response.text()

    console.log('Short code:', shortCode)

    // alert(`Your short URL is: ${shortCode}`)
    setShortUrl(`http://localhost:8080/url/${shortCode}`)

  } catch (err) {
    console.error(err)
    setError('Unable to connect to the backend')
  }
  }

  return (
    <div className="app">
      <div className="container">
        <h1>URL Shortener</h1>

        <p>Shorten your long URLs in seconds.</p>

        <form onSubmit={handleSubmit} noValidate>
          <label>Long URL</label>

          <input
            type="url"
            placeholder="https://example.com/very-long-url"
            value={originalUrl}
            onChange={(e) => setOriginalUrl(e.target.value)}
          />

          <label>Custom short name (optional)</label>

          <input
            type="text"
            placeholder="my-custom-link"
            value={customCode}
            onChange={(e) => setCustomCode(e.target.value)}
          />

          {/* Error will appear only when error has a value */}
          {error && <h2>ERROR: {error}</h2>}

          <button type="submit">
            Shorten URL
          </button>

          {shortUrl && (
  <div>
    <h3>Your Short URL:</h3>
    <a href={shortUrl} target="_blank">
      {shortUrl}
    </a>
  </div>
)}
        </form>
      </div>
    </div>
  )
}

export default App  