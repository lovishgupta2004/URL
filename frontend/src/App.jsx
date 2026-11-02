import { useState } from 'react'
import './App.css'

function App() {
  const [originalUrl, setOriginalUrl] = useState('')
  const [customCode, setCustomCode] = useState('')
  const [error, setError] = useState('')
  const [shortUrl, setShortUrl] = useState('')
  const [copied, setCopied] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()

    // Clear previous error and result
    setError('')
    setShortUrl('')
    setCopied(false)

    // Check if URL is empty
    if (!originalUrl.trim()) {
      setError('Please enter a URL')
      return
    }

    try {
      const trimmedOriginalUrl = originalUrl.trim()
      const trimmedCustomCode = customCode.trim()

      // Prepare request depending on whether custom code exists
      const requestBody = trimmedCustomCode
        ? {
            headers: {
              'Content-Type': 'application/json',
            },
            body: JSON.stringify({
              originalUrl: trimmedOriginalUrl,
              customUrl: trimmedCustomCode,
            }),
          }
        : {
            headers: {
              'Content-Type': 'text/plain',
            },
            body: trimmedOriginalUrl,
          }

      // Call Spring Boot backend
      const response = await fetch('http://localhost:8080/url', {
        method: 'POST',
        ...requestBody,
      })

      // Handle backend errors
      if (!response.ok) {
        let message = await response.text()

        try {
          const errorResponse = JSON.parse(message)

          if (response.status === 409) {
            message =
              'This custom short name is already taken. Please choose another one.'
          } else {
            message =
              errorResponse.detail ||
              errorResponse.message ||
              'Failed to shorten URL'
          }
        } catch {
          if (response.status === 409) {
            message =
              'This custom short name is already taken. Please choose another one.'
          } else {
            message = 'Failed to shorten URL'
          }
        }

        setError(message)
        return
      }

      // Get short code returned by backend
      const shortCode = await response.text()

      // Create complete short URL
      setShortUrl(`http://localhost:8080/url/${shortCode}`)

    } catch (err) {
      console.error(err)
      setError('Unable to connect to the backend')
    }
  }

  // Copy generated URL to clipboard
  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(shortUrl)

      setCopied(true)

      // Change button back after 2 seconds
      setTimeout(() => {
        setCopied(false)
      }, 20000)

    } catch (err) {
      console.error('Failed to copy:', err)
      setError('Failed to copy the URL')
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

          {/* Show error message */}
          {error && (
            <p className="error-message">
              ❌ {error}
            </p>
          )}

          <button type="submit">
            Shorten URL
          </button>

          {/* Show generated short URL */}
          {shortUrl && (
            <div className="result">
              <h3>Your Short URL:</h3>

              <div className="short-url-container">
                <a
                  href={shortUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  {shortUrl}
                </a>

                <button
                  type="button"
                  onClick={handleCopy}
                  className="copy-button"
                >
                  {copied ? 'Copied! ✓' : '📋 Copy'}
                </button>
              </div>
            </div>
          )}
        </form>
      </div>
    </div>
  )
}

export default App