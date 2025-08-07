'use client'

import { useState, useEffect } from 'react'
import { ChevronLeft, ChevronRight, Download } from 'lucide-react'

interface FrameViewerProps {
  videoId: string;
  frameCount: number;
}

export default function FrameViewer({ videoId, frameCount }: FrameViewerProps) {
  const [currentFrame, setCurrentFrame] = useState(1)
  const [frameList, setFrameList] = useState<string[]>([])

  // Buscar lista real de frames da API
  useEffect(() => {
    const fetchFrameList = async () => {
      try {
        const response = await fetch(`/api/frames/${videoId}/list`)
        if (response.ok) {
          const frameNames: string[] = await response.json()
          const frameUrls = frameNames.map(name => `/api/frames/${videoId}/${name}`)
          setFrameList(frameUrls)
        } else {
          // Fallback para nomes sequenciais se API falhar
          const frames = Array.from({ length: frameCount }, (_, i) => {
            const frameNumber = (i + 1).toString().padStart(3, '0')
            return `/api/frames/${videoId}/frame_${frameNumber}.jpg`
          })
          setFrameList(frames)
        }
      } catch (error) {
        console.error('Erro ao buscar frames:', error)
        // Fallback para nomes sequenciais
        const frames = Array.from({ length: frameCount }, (_, i) => {
          const frameNumber = (i + 1).toString().padStart(3, '0')
          return `/api/frames/${videoId}/frame_${frameNumber}.jpg`
        })
        setFrameList(frames)
      }
    }

    if (videoId && frameCount > 0) {
      fetchFrameList()
    }
  }, [videoId, frameCount])

  const nextFrame = () => {
    if (currentFrame < frameCount) {
      setCurrentFrame(currentFrame + 1)
    }
  }

  const prevFrame = () => {
    if (currentFrame > 1) {
      setCurrentFrame(currentFrame - 1)
    }
  }

  if (frameCount === 0) {
    return (
      <div className="text-center text-gray-500 py-8">
        <p>Nenhum frame disponível para exibição.</p>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      {/* Header simples */}
      <div className="text-center">
        <span className="text-sm text-gray-600">
          {frameCount} frames extraídos
        </span>
      </div>

      {/* Visualização atual */}
      <div className="bg-white rounded-lg border p-4">
        {frameList[currentFrame - 1] && (
          <img
            src={frameList[currentFrame - 1]}
            alt={`Frame ${currentFrame}`}
            className="w-full h-auto max-h-64 object-contain mx-auto rounded"
            onError={(e) => {
              const target = e.target as HTMLImageElement
              target.src = `data:image/svg+xml;base64,${btoa(`
                <svg width="400" height="300" xmlns="http://www.w3.org/2000/svg">
                  <rect width="100%" height="100%" fill="#f3f4f6"/>
                  <text x="50%" y="50%" font-family="Arial" font-size="16" fill="#6b7280" text-anchor="middle" dy=".3em">
                    Frame ${currentFrame}
                  </text>
                </svg>
              `)}`
            }}
          />
        )}
      </div>

      {/* Controles minimalistas */}
      <div className="flex items-center justify-center gap-4">
        <button
          onClick={prevFrame}
          disabled={currentFrame === 1}
          className="p-2 bg-gray-100 rounded-full hover:bg-gray-200 disabled:opacity-50 transition-colors"
        >
          <ChevronLeft size={20} />
        </button>
        
        <span className="text-sm font-medium min-w-[100px] text-center">
          {currentFrame} / {frameCount}
        </span>
        
        <button
          onClick={nextFrame}
          disabled={currentFrame === frameCount}
          className="p-2 bg-gray-100 rounded-full hover:bg-gray-200 disabled:opacity-50 transition-colors"
        >
          <ChevronRight size={20} />
        </button>
      </div>

      {/* Slider simples */}
      <div className="px-4">
        <input
          type="range"
          min="1"
          max={frameCount}
          value={currentFrame}
          onChange={(e) => setCurrentFrame(parseInt(e.target.value))}
          className="w-full h-1 bg-gray-200 rounded-lg appearance-none cursor-pointer"
        />
      </div>

      {/* Grid minimalista de todos os frames */}
      <div className="grid grid-cols-4 md:grid-cols-6 lg:grid-cols-8 gap-2">
        {frameList.map((frameSrc, index) => (
          <div
            key={index}
            className={`relative cursor-pointer rounded overflow-hidden aspect-video bg-gray-100 ${
              currentFrame === index + 1 ? 'ring-2 ring-blue-500' : 'hover:ring-1 hover:ring-gray-400'
            }`}
            onClick={() => setCurrentFrame(index + 1)}
          >
            <img
              src={frameSrc}
              alt={`Frame ${index + 1}`}
              className="w-full h-full object-cover"
              onError={(e) => {
                const target = e.target as HTMLImageElement
                target.src = `data:image/svg+xml;base64,${btoa(`
                  <svg width="100" height="75" xmlns="http://www.w3.org/2000/svg">
                    <rect width="100%" height="100%" fill="#f3f4f6"/>
                    <text x="50%" y="50%" font-family="Arial" font-size="10" fill="#6b7280" text-anchor="middle" dy=".3em">
                      ${index + 1}
                    </text>
                  </svg>
                `)}`
              }}
            />
            
            {/* Número do frame */}
            <div className="absolute bottom-0 right-0 bg-black bg-opacity-50 text-white text-xs px-1 rounded-tl">
              {index + 1}
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
