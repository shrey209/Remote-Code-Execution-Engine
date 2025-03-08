package main

import (
	"log"
	"net/http"
	"strings"

	"github.com/gin-gonic/gin"
)

type Request struct {
	Code  string `json:"code" binding:"required"`
	Input string `json:"input"`
	Lang  string `json:"lang" binding:"required"`
}

type Output struct {
	IsError  bool   `json:"isError"`
	Response string `json:"response"`
}

func parseRequest(c *gin.Context) {
	var req Request

	if err := c.ShouldBindJSON(&req); err != nil {
		log.Println("Invalid request format:", err)
		c.JSON(http.StatusBadRequest, Output{
			IsError:  true,
			Response: "Invalid request format",
		})
		return
	}

	log.Printf("Received Code:\n%s\n", req.Code)
	log.Printf("Input Data: %s\n", req.Input)
	log.Printf("Language: %s\n", req.Lang)

	var output string
	var err error

	switch strings.ToLower(req.Lang) {
	case "cpp", "c++":
		output, err = cppHandler(req.Code, req.Input)
	case "python", "py":
		output, err = pythonHandler(req.Code, req.Input)
	default:
		log.Println("Unsupported language:", req.Lang)
		c.JSON(http.StatusBadRequest, Output{
			IsError:  true,
			Response: "Unsupported language",
		})
		return
	}

	if err != nil {
		log.Println("Execution error:", err)
		c.JSON(http.StatusInternalServerError, Output{
			IsError:  true,
			Response: err.Error(),
		})
		return
	}

	c.JSON(http.StatusOK, Output{
		IsError:  false,
		Response: output,
	})
}
